package uk.co.whatsa.research.presentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.whatsa.research.ResearchRuntimeException;
import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.HtmlWebPageResource;
import uk.co.whatsa.research.model.JavaScriptResource;
import uk.co.whatsa.research.model.JsWebPageResource;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;
import uk.co.whatsa.research.model.WebPageVersion;

/**
 * Handles the capture (storage) of a web page. Instances of the class
 * are passed to an {@link ExecutorCompletionService} which processes
 * the instance in a separate Thread via the class's {@link Callable}
 * interface implementation.
 * 
 * The class makes use of the linux {@code wget} command to retrieve a
 * web page's resource and convert links within the page to reference
 * the local resources. It is also responsible for reading the files
 * and creating appropriate WebPageResource instances and ensuring
 * they are persisted.
 */
public class CaptureWebPage implements Callable<CaptureWebPageResult> {
    /** Used to log error and debug information. */
    private static final Logger LOG = LoggerFactory.getLogger(CaptureWebPage.class);

    /**
     * The folder name prefix prepended to the temporary file name.
     */
    private static final String TEMP_FOLDER_PREFIX = File.separator + "tmp" + File.separator + "page.";

    /** The name of the log file created by wget. */
    private static final String WGET_LOG_FILENAME = "wget.log";

    /**
     * Determines the size of a buffer used to read the contents of
     * each web page resource.
     */
    private static final int FILE_INPUT_BUFFER_SIZE = 51200;

    /** The parameters entered by the user on the web page. */
    private final WebPageModelAttribute parameters;

    /**
     * A randomly generated value used to name the folder into which
     * the web page will be downloaded by wget.
     */
    private final String randomName;

    /**
     * The name of the temporary folder used to hold the web page data
     * downloaded by the {code wget} command.
     * 
     * @see #randomName
     */
    private final String tempFolderName;

    /**
     * The name of the file used by {@code wget} to log the output.
     * The file is tailed in the web UI.
     */
    private final String outputLogFilename;

    /**
     * Used to determine the mime type of of the web page resources.
     */
    @Autowired
    private MimetypesFileTypeMap mimetypesFileTypeMap;

    /**
     * A data access object that provides access to the persistent
     * store holding the web research data.
     */
    @Autowired
    private ResearchDAO researchDAO;

    /**
     * @param parameters the parameters defining the page to be
     *            captured which were sent from the browser. The page
     *            contains a single WebPageVersion that contains any
     *            parameters additional to the web page URL.
     */
    public CaptureWebPage(final WebPageModelAttribute parameters) {
        this.parameters = parameters;
        final int numberOfBits = 130;
        final int radix = 36; // 0-9 plus a-z
        this.randomName = new BigInteger(numberOfBits, new SecureRandom()).toString(radix);

        final StringBuilder sb = new StringBuilder(TEMP_FOLDER_PREFIX).append(randomName).append(File.separator);
        tempFolderName = sb.toString();
        outputLogFilename = sb.append(WGET_LOG_FILENAME).toString();
    }

    /**
     * @return the logger for this class
     */
    static final Logger getLogger() {
        return LOG;
    }

    /**
     * @return {@link #outputLogFilename}
     */
    public final String getOutputLogFilename() {
        return outputLogFilename;
    }

    /**
     * @return {@link #randomName}
     */
    public final String getRandomName() {
        return randomName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CaptureWebPageResult call() {
        final CaptureWebPageResult result = new CaptureWebPageResult();
        try {
            final WebPage webPage = retrieveWebPage();
            if (webPage == null) {
                LOG.error("Failed to retrieve web page");
            } else {
                researchDAO.saveWebPage(webPage);
            }
        } catch (ProcessFailedException e) {
            result.setProcessFailedException(e);
        }
        return result;
    }

    /**
     * @return the WebPage model in preparation for it to be
     *         persisted. The web page is downloaded, along with its
     *         associated resources. The downloaded version is
     *         modified to use relative URLs where possible rather
     *         than the original internet links
     */
    private WebPage retrieveWebPage() {
        WebPage webPage = null;
        File tempFolder = null;
        if (parameters.getUrl() != null) {
            try {
                final File tempParentFolder = createTempFolder();
                tempFolder = new File(tempParentFolder, "1");
                tempFolder.mkdir();

                final List<String> commands = new ArrayList<String>();
                commands.add("wget");

                // Don't download robots.txt which may prevent some
                // resources being downloaded
                commands.add("-e robots=off");

                /*
                 * This option causes wget to download all the files
                 * that are necessary to properly display a given HTML
                 * page. This includes such things as inlined images,
                 * sounds, and referenced stylesheets.
                 */
                commands.add("-p");

                /*
                 * After the download is complete, convert the links
                 * in the document to make them suitable for local
                 * viewing. This affects not only the visible
                 * hyperlinks, but any part of the document that links
                 * to external content, such as embedded images, links
                 * to style sheets, hyperlinks to non-HTML content,
                 * etc.
                 */
                commands.add("-k");

                /*
                 * Enable spanning across hosts when doing recursive
                 * retrieving.
                 */
                commands.add("-H");

                /*
                 * If this is set to on, wget will not skip the
                 * content when the server responds with a http status
                 * code that indicates error.
                 */
                commands.add("--content-on-error");

                /*
                 * Writing the output log has the added benefit that
                 * it allows some downloads to complete when they
                 * would otherwise appear to hang (the site
                 * http://www.davidicke.com/headlines being a case in
                 * point)
                 */
                commands.add(new StringBuilder("--output-file=").append(outputLogFilename).toString());

                // ... and finally, the URL of the page the user
                // requested to be stored.
                commands.add(parameters.getUrl());

                final ProcessBuilder pb = new ProcessBuilder(commands);
                pb.directory(tempFolder);
                final Process p = pb.start();
                p.waitFor();

                CaptureHelper.deleteUnwantedFolders(tempFolder, parameters.getDomainName());

                final byte[] buffer = new byte[FILE_INPUT_BUFFER_SIZE];
                final int exitValue = p.exitValue();
                if (exitValue == WgetError.SUCCESS.ordinal() || exitValue == WgetError.SERVER_ERROR_RESPONSE.ordinal()
                        || exitValue == WgetError.NETWORK_FAILURE.ordinal()) {
                    final WebPageVersion webPageVersion = new WebPageVersion();
                    webPageVersion.setRecordedDate(parameters.getRecordedDate());
                    createWebPageResources(webPageVersion, buffer, CaptureHelper.getResourcePath(parameters.getUrl()),
                            tempFolder, tempFolder);
                    webPage = new WebPage(parameters.getUrl(), parameters.getComment());
                    webPage.addVersion(webPageVersion);
                } else {
                    handleFailedWget(exitValue, tempParentFolder);
                }
            } catch (IOException e) {
                throw new ResearchRuntimeException(e);
            } catch (InterruptedException e) {
                throw new ResearchRuntimeException(e);
            } finally {
                if (!CaptureHelper.deleteFolder(tempFolder)) {
                    LOG.warn("failed to delete temporary folder: ", tempFolder.getPath());
                }
            }
        }
        return webPage;
    }

    /**
     * Called recursively to instantiate {@link WebPageResource}
     * instances corresponding to the files downloaded for the web
     * page.
     * 
     * @param webPageVersion the parent {@link WebPage} instance
     * @param buffer a buffer used to hold the file data before it is
     *            stored as a {@code WebPageResource}
     * @param mainResourcePath the path of the main html web page
     * @param rootFolder the root folder used to store the downloaded
     *            web page and its associated resources
     * @param resourcesFolder the folder containing the files and
     *            subfolders generated when the web page was
     *            downloaded
     */
    private void createWebPageResources(final WebPageVersion webPageVersion, final byte[] buffer,
            final String mainResourcePath, final File rootFolder, final File resourcesFolder) {
        if (!resourcesFolder.isDirectory()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("provided resources directory was not a folder (" + resourcesFolder.getPath() + ")");
            }
            return;
        }
        for (final File file : resourcesFolder.listFiles()) {
            if (file.isDirectory()) {
                createWebPageResources(webPageVersion, buffer, mainResourcePath, rootFolder, file);
            } else {
                final WebPageResource resource = createWebPageResource(rootFolder, file, buffer);
                if (resource == null) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("failed to create resource from file: " + file.getPath());
                    }
                } else {
                    webPageVersion.addResource(resource);
                    if (webPageVersion.getMainResource() == null && resource instanceof HtmlWebPageResource) {
                        final String resourcePathWithoutParameters = CaptureHelper.stripParameters(resource.getPath());
                        if (mainResourcePath.equals(resourcePathWithoutParameters)) {
                            webPageVersion.setMainResource(resource);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param rootFolder the root folder for the wget process
     * @param file the resource file created by wget
     * @param buffer a common buffer used to buffer file data
     * @return a WebPageResource instance corresponding to the given
     *         file
     */
    private WebPageResource createWebPageResource(final File rootFolder, final File file, final byte[] buffer) {
        final String mimeContentType = mimetypesFileTypeMap
                .getContentType(CaptureHelper.getFilenameWithoutParameters(file.getName()).toLowerCase());

        final WebPageResourceType resourceType = WebPageResourceType.getWebPageResourceType(mimeContentType);
        final WebPageResource resource = resourceType.createResource();
        final String resourcePath = CaptureHelper.getResourcePath(rootFolder, file);
        resource.setPath(resourcePath);
        resource.setMimeContentType(mimeContentType);

        boolean readResourceData = true;
        if (resourceType.equals(WebPageResourceType.JS)) {
            // JsWebPageResource instances reference a shared
            // JavaScriptResource instance
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
            final byte[] resourceData = CaptureHelper.readResourceData(file, buffer);
            // If we successfully read the contents of the file
            if (resourceData != null) {
                resource.setResourceData(resourceData);
            }
        }

        return resource;
    }

    /**
     * Throws a {@link ProcessFailedException} which encapsulates an
     * error message string detailing the cause of the problem.
     * 
     * @param errorCode the code that was returned from the wget
     *            command
     * @param logFileFolder the folder containing the wget log file
     */
    private static void handleFailedWget(final int errorCode, final File logFileFolder) {
        // Read the contents of the log file into a String
        final List<String> errorMessageLines = new LinkedList<String>();
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
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }

        throw new ProcessFailedException(errorCode, errorMessageLines);
    }

    /**
     * Creates a temporary folder which is used to store the
     * downloaded web pages.
     * 
     * @return a File object representing a folder to store the
     *         downloaded web pages.
     */
    private File createTempFolder() {
        final File tempFolder = new File(tempFolderName);
        tempFolder.mkdir();
        return tempFolder;
    }

}
