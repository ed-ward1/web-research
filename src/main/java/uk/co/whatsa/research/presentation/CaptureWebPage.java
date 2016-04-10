package uk.co.whatsa.research.presentation;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.HtmlWebPageResource;
import uk.co.whatsa.research.model.JavaScriptResource;
import uk.co.whatsa.research.model.JsWebPageResource;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;

/**
 * Handles the capture (storage) of a web page.
 */
public class CaptureWebPage implements Callable<CaptureWebPage.Result> {
	private static final Logger LOG = LoggerFactory.getLogger(CaptureWebPage.class);

	private static final String TEMP_FOLDER_PREFIX = File.separator + "tmp" + File.separator + "page.";

	/** The name of the log file created by wget. */
	private static final String WGET_LOG_FILENAME = "wget.log";

	private static final int FILE_INPUT_BUFFER_SIZE = 51200;

	/** The name given to resources when the URL did not contain a name. */
	private static final String DEFAULT_FILENAME = "index.html";

	/** The value used to indicate End Of Stream when reading bytes from a stream. */
	private static final int EOS = -1;

	/** The exit values of a wget process (ordinals are significant). */
	private static enum WgetError {
		SUCCESS, GENERIC_ERROR, PARSE_ERROR, FILE_IO_ERROR, NETWORK_FAILURE, SSL_VERIFICATION_FAILURE, USERNAME_PASSWORD_AUTHENTICATION_FAILURE, PROTOCOL_ERRORS, SERVER_ERROR_RESPONSE;
	}

	private final WebPage webPage;

	private final String randomName;

	private final String tempFolderName;
	
	private final String outputLogFilename;

	@Autowired
	private MimetypesFileTypeMap mimetypesFileTypeMap;

	@Autowired
	private ResearchDAO researchDAO;

	/**
	 * Since the page is captured asynchronously (in a separate thread) this class encapsulates the
	 * results of the page capture process.
	 */
	public static class Result {
		private ProcessFailedException processFailedException;

		public ProcessFailedException getProcessFailedException() {
			return processFailedException;
		}

		public void setProcessFailedException(ProcessFailedException processFailedException) {
			this.processFailedException = processFailedException;
		}
	};

	/**
	 * @param webPage the parameters defining the page to be captured
	 */
	public CaptureWebPage(WebPage webPage) {
		this.webPage = webPage;

		this.randomName = new BigInteger(130, new SecureRandom()).toString(36);

		final StringBuilder sb = new StringBuilder(TEMP_FOLDER_PREFIX).append(randomName).append(File.separator);
		tempFolderName = sb.toString();
		outputLogFilename = sb.append(WGET_LOG_FILENAME).toString();
	}

	public String getOutputLogFilename() {
		return outputLogFilename;
	}

	public String getRandomName() {
		return randomName;
	}

	@Override
	public Result call() throws Exception {
		Result result = new Result();
		try {
			retrieveWebPage(webPage);
			researchDAO.saveWebPage(webPage);
		} catch (ProcessFailedException e) {
			result.setProcessFailedException(e);
		}
		return result;
	}

	/**
	 * Initialises the WebPage model in preparation for it to be persisted. The web page is
	 * downloaded, along with its associated resources. The downloaded version is modified to use
	 * relative URLs where possible rather than the original internet links
	 * 
	 * @param webPage the page to initialise, which contains the "capture" details entered by the
	 *            user
	 * @return the initialised web page
	 */
	void retrieveWebPage(final WebPage webPage) {
		File tempFolder = null;
		if (webPage.getUrl() != null) {
			try {
				final File tempParentFolder = createTempFolder();
				tempFolder = new File(tempParentFolder, "1");
				tempFolder.mkdir();

				List<String> commands = new ArrayList<String>();
				commands.add("wget");
				// Don't download robots.txt which may prevent some resources being downloaded
				commands.add("-e robots=off");
				/*
				 * This option causes wget to download all the files that are necessary to properly
				 * display a given HTML page. This includes such things as inlined images, sounds,
				 * and referenced stylesheets.
				 */
				commands.add("-p");
				/*
				 * After the download is complete, convert the links in the document to make them
				 * suitable for local viewing. This affects not only the visible hyperlinks, but any
				 * part of the document that links to external content, such as embedded images,
				 * links to style sheets, hyperlinks to non-HTML content, etc.
				 */
				commands.add("-k");
				/*
				 * Disable generation of host-prefixed directories. By default, invoking Wget with
				 * -r http://fly.srk.fer.hr/ will create a structure of directories beginning with
				 * fly.srk.fer.hr/. This option disables such behaviour.
				 */
				commands.add("-nH");

				/*
				 * If this is set to on, wget will not skip the content when the server responds
				 * with a http status code that indicates error.
				 */
				commands.add("--content-on-error");

				/*
				 * Writing the output log has the added benefit that it allow some downloads to
				 * complete when they would otherwise appear to hang (the site
				 * http://www.davidicke.com/headlines being a case in point)
				 */
				commands.add(new StringBuilder("--output-file=").append(outputLogFilename).toString());

				// ... and finally, the URL of the page the user requested to be stored.
				commands.add(webPage.getUrl());

				final ProcessBuilder pb = new ProcessBuilder(commands);
				pb.directory(tempFolder);
				final Process p = pb.start();
				p.waitFor();

				final byte[] buffer = new byte[FILE_INPUT_BUFFER_SIZE];
				final int exitValue = p.exitValue();
				if (exitValue == WgetError.SUCCESS.ordinal()
						|| exitValue == WgetError.SERVER_ERROR_RESPONSE.ordinal()) {
					createWebPageResources(webPage, buffer, getResourcePath(webPage.getUrl()), tempFolder, tempFolder);
				} else {
					handleFailedWget(exitValue, tempParentFolder, buffer);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (RuntimeException e) {
				LOG.error(e.getMessage(), e);
			} finally {
				if (!deleteTempFolder(tempFolder)) {
					LOG.warn("failed to delete temporary folder: ", tempFolder.getPath());
				}
			}
		}
	}

	/**
	 * Called recursively to instantiate {@link WebPageResource} instances corresponding to the
	 * files downloaded for the web page.
	 * 
	 * @param webPage the parent {@link WebPage} instance
	 * @param buffer a buffer used to hold the file data before it is stored as a
	 *            {@code WebPageResource}
	 * @param mainResourcePath the path of the main html web page
	 * @param rootFolder the root folder used to store the downloaded web page and its associated
	 *            resources
	 * @param resourcesFolder the folder containing the files and subfolders generated when the web
	 *            page was downloaded
	 */
	private void createWebPageResources(WebPage webPage, final byte[] buffer, final String mainResourcePath,
			final File rootFolder, final File resourcesFolder) {
		if (!resourcesFolder.isDirectory()) {
			LOG.error("provided resources directory was not a folder (" + resourcesFolder.getPath() + ")");
			return;
		}
		for (File file : resourcesFolder.listFiles()) {
			if (file.isDirectory()) {
				createWebPageResources(webPage, buffer, mainResourcePath, rootFolder, file);
			} else {
				final WebPageResource resource = createWebPageResource(rootFolder, file, buffer);
				if (resource != null) {
					webPage.addResource(resource);
					if (webPage.getMainResource() == null && resource instanceof HtmlWebPageResource) {
						final String resourcePathWithoutParameters = stripParameters(resource.getPath());
						if (mainResourcePath.equals(resourcePathWithoutParameters)) {
							webPage.setMainResource(resource);
						}
					}
				} else if (LOG.isWarnEnabled()) {
					LOG.warn("failed to create resource from file: " + file.getPath());
				}
			}
		}
	}

	/**
	 * @param rootFolder the root folder for the wget process
	 * @param file the resource file created by wget
	 * @param buffer a common buffer used to buffer file data
	 * @return a WebPageResource instance corresponding to the given file
	 */
	private WebPageResource createWebPageResource(File rootFolder, File file, byte[] buffer) {
		final String mimeContentType = mimetypesFileTypeMap
				.getContentType(getFilenameWithoutParameters(file.getName()).toLowerCase());

		final WebPageResourceType resourceType = WebPageResourceType.getWebPageResourceType(mimeContentType);
		final WebPageResource resource = resourceType.createResource();
		final String resourcePath = getResourcePath(rootFolder, file);
		resource.setPath(resourcePath);
		resource.setMimeContentType(mimeContentType);

		boolean readResourceData = true;
		if (resourceType.equals(WebPageResourceType.JS)) {
			// JsWebPageResource instances reference a shared JavaScriptResource instance
			final JsWebPageResource jsWebPageResource = (JsWebPageResource) resource;
			JavaScriptResource javaScript = researchDAO.findJavaScriptResourceWithName(file.getName());
			if (javaScript == null) {
				javaScript = new JavaScriptResource();
				javaScript.setName(file.getName());
			} else {
				readResourceData = false;
			}
			jsWebPageResource.setJavaScript(javaScript);
		}

		if (readResourceData) {
			final byte[] resourceData = readResourceData(file, buffer);
			// If we successfully read the contents of the file
			if (resourceData != null) {
				resource.setResourceData(resourceData);
			}
		}

		return resource;
	}

	/**
	 * If the URL does not contain a reference to a filename then the default (index.html) is
	 * returned.
	 * 
	 * @param filename the resource / filename possibly with trailing parameters.
	 * @return the filename (resource name) portion of a partial URL.
	 */
	private static String getFilenameWithoutParameters(String filename) {
		String filenameWithoutParameters = stripParameters(filename);
		final int characterNotFound = -1;
		if (filenameWithoutParameters.indexOf('.') == characterNotFound) {
			filenameWithoutParameters = DEFAULT_FILENAME;
		}
		return filenameWithoutParameters;
	}

	/**
	 * @param withParameters a partial URL string possibly containing parameter after the resource
	 *            name
	 * @return the input string stripped of any trailing parameters (after the ? or #)
	 */
	private static String stripParameters(final String withParameters) {
		int firstQuestionMark = withParameters.indexOf('?');
		int firstHashMark = withParameters.indexOf('#');

		final int notFound = -1;

		int end = withParameters.length();
		if (firstQuestionMark != notFound) {
			end = firstQuestionMark;
		}
		if (firstHashMark != notFound && firstHashMark < end) {
			end = firstHashMark;
		}
		return withParameters.substring(0, end);
	}

	/**
	 * The path should always equal the path of the {@link #mainResource}. This method is needed to
	 * find the {@link #mainResource} before the property has been set.
	 * 
	 * @param webPageUrl the URL provided when capturing the web page
	 * @return The path of the resource associated with this web page's main html file or
	 *         {@code null} if there is an error.
	 */
	private static String getResourcePath(String webPageUrl) {
		// Remove the preceding protocol and domain
		final int notFound = -1;
		int slashOffset = 0;

		for (int i = 0; i < 3 && slashOffset != notFound; i++, slashOffset++) {
			slashOffset = webPageUrl.indexOf('/', slashOffset);
		}
		String resourcePath = null;
		if (slashOffset != notFound) {
			resourcePath = webPageUrl.substring(slashOffset);

			int end = resourcePath.length();
			int hashOffset = resourcePath.indexOf('#');
			if (hashOffset != notFound) {
				end = hashOffset;
			}
			int queryOffset = resourcePath.indexOf('?');
			if (queryOffset != notFound && queryOffset < end) {
				end = queryOffset;
			}
			resourcePath = resourcePath.substring(0, end);
			if (resourcePath.endsWith("/") || resourcePath.length() == 0) {
				resourcePath = new StringBuilder(resourcePath).append(DEFAULT_FILENAME).toString();
			}
		} else {
			resourcePath = DEFAULT_FILENAME;
		}

		return resourcePath;
	}

	/**
	 * @param rootFolderPath the root folder in which wget created the resources
	 * @param filepath the path of the file generated by wget
	 * @return a relative resource path based on the file path of the resource object created by
	 *         wget.
	 */
	private static String getResourcePath(File rootFolder, File file) {
		final String rootFolderPath = rootFolder.getPath();
		final String filepath = file.getPath();
		if (rootFolderPath.length() > filepath.length()) {
			LOG.error("resource root folder path was longer than the resource path", rootFolderPath);
			return filepath;
		}
		return filepath.substring(rootFolderPath.length() + 1, filepath.length());
	}

	/**
	 * @param file the resource file to be read
	 * @return the contents of the resource file as one very long byte array
	 */
	private static byte[] readResourceData(File file, byte[] buffer) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("reading resource data from file: " + file.getPath());
		}
		byte[] resourceData = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int bytesRead = fis.read(buffer);
			ByteArrayOutputStream baos = new ByteArrayOutputStream((int) file.length());
			while (bytesRead != EOS) {
				baos.write(buffer, 0, bytesRead);
				bytesRead = fis.read(buffer);
			}
			resourceData = baos.toByteArray();
		} catch (IOException e) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					if (e instanceof RuntimeException) {
						throw (RuntimeException) e;
					}
					throw new RuntimeException(e);
				}
			}
		}
		return resourceData;
	}

	/**
	 * Throws a {@link ProcessFailedException} which encapsulates an error message string detailing
	 * the cause of the problem.
	 * 
	 * @param logFileFolder the folder containing the wget log file
	 * @param buffer a buffer used to read the contents of the log file
	 */
	private static void handleFailedWget(int errorCode, File logFileFolder, byte[] buffer) {
		// Read the contents of the log file into a String
		List<String> errorMessageLines = new LinkedList<String>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(new File(logFileFolder, WGET_LOG_FILENAME));
			br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				errorMessageLines.add(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			errorMessageLines.add("wget failed - failed to open log file " + WGET_LOG_FILENAME);
		} catch (IOException e) {
			errorMessageLines.add("wget failed - i/o exception whilst reading log file");
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception e) {
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
			}
		}

		throw new ProcessFailedException(errorCode, errorMessageLines);
	}

	/**
	 * Creates a temporary folder which is used to store the downloaded web pages.
	 * 
	 * @return a File object representing a folder to store the downloaded web pages.
	 */
	private File createTempFolder() {
		File tempFolder = new File(tempFolderName);
		tempFolder.mkdir();
		return tempFolder;
	}

	/**
	 * Deletes a folder by individually deleting each file and subfolder folder.
	 * 
	 * @param tempFolder the folder to be deleted
	 * @return true of the folder was deleted successfully
	 */
	private boolean deleteTempFolder(final File tempFolder) {
		boolean success = false;
		try {
			if (tempFolder.isDirectory()) {
				for (File file : tempFolder.listFiles()) {
					if (file.isDirectory()) {
						deleteTempFolder(file);
					} else {
						file.delete();
					}
				}
				success = tempFolder.delete();
			}
		} catch (Throwable t) {
			LOG.debug("failed to delete folder", tempFolder);
		}

		return success;
	}
}
