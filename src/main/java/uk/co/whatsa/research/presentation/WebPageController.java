package uk.co.whatsa.research.presentation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;

/**
 * This controller contains request handlers responsible for the capture and display of website web
 * pages.
 */
@Controller
@RequestMapping(path = "/page")
public class WebPageController {
	private static final Logger LOG = LoggerFactory.getLogger(WebPageController.class);

	/** All URLs are encoded as ASCII. */
	private static final String URL_CHAR_ENCODING = "US-ASCII";

	@Autowired
	private ResearchDAO researchDAO;

	@Autowired
	private ExecutorCompletionService<CaptureWebPage.Result> completionService;
	
	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Handles the request to get the "capture web page" page (HTTP GET).
	 * 
	 * @return
	 */
	@RequestMapping(path = "/capture", method = RequestMethod.GET)
	public String getCaptureWebPage(Model model) {
		WebPage webPage = new WebPage();
		webPage.setRecordedDate(DateTime.now());
		webPage.setPageDate(DateTime.now());
		model.addAttribute(webPage);
		return "captureWebPage";
	}

	@RequestMapping(path="/newTab", method = RequestMethod.POST)
	public String fragment(@RequestParam String selector, Model model) {
		model.addAttribute("selector", selector);
		return "fragments/pageTemplate :: tabFrag";
	}

	@RequestMapping(path = "/captureAjax", method = RequestMethod.POST)
	public @ResponseBody TailedFile postCaptureAjaxWebPage(@ModelAttribute WebPage webPage, BindingResult bindingResult) {
		TailedFile tailedFile = new TailedFile();
		if (!bindingResult.hasErrors()) {
			CaptureWebPage captureWebPage = beanFactory.getBean(CaptureWebPage.class, webPage);
			completionService.submit(captureWebPage);
			tailedFile.setSelector(captureWebPage.getRandomName());
			tailedFile.setFileName(captureWebPage.getOutputLogFilename());
		}

		return tailedFile;
	}

	/**
	 * A handy way to display a captured web page in the browser, using just the web page's database
	 * id. The method redirects the browser to the page by appending the page's resource path to the
	 * request URL.
	 * 
	 * @param webPageId the id of the WebPage instance in the database
	 * @param request the servlet request
	 * @return a redirect URL path containing the page's resource path.
	 */
	@RequestMapping(path = "{webPageId}")
	public String displayWebPage(@PathVariable ID webPageId, HttpServletRequest request) {
		WebPage webPage = researchDAO.getWebPage(webPageId);
		String view = new StringBuilder("redirect:/page/").append(webPageId).append("/")
				.append(webPage.getMainResource().getPath()).toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Serving webpage for id = " + webPageId + ", view = " + view);
		}
		return view;
	}

	/**
	 * Displays a capture web page (or web page resource) in the browser.
	 * 
	 * @param webPageId the database id of the web page
	 * @param request the servlet request
	 * @param output the {@code OutputStream} to write the web page resource to
	 */
	@RequestMapping(path = "{webPageId}/**")
	public void displayWebPage(@PathVariable ID webPageId, HttpServletRequest request, OutputStream output) {
		byte[] resourceData = null;
		try {
			final String resourcePath = getResourcePath(request);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Serving webpage for id = " + webPageId + ", url = " + resourcePath);
			}
			final WebPageResource resource = researchDAO.getWebPageResource(webPageId, resourcePath);
			if (resource != null) {
				resourceData = resource.getResourceData();
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error("no resource found for : " + webPageId, e);
		}

		if (resourceData == null) {
			resourceData = "no resource found".getBytes();
		}

		try {
			output.write(resourceData);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Examples of input request and output string ...
	 * 
	 * <pre>
	 *  {@code
	 * INPUT: http://localhost:8080/research/page/12/wiki/2015/12/the-page-with-the-data/
	 * OUTPUT: research/page/12/wiki/2015/12/the-page-with-the-data/index.html
	 *
	 * INPUT: http://localhost:8080/research/page/12/wiki/2015/12/the-page-with-the-data/#the-info
	 * OUTPUT: research/page/12/wiki/2015/12/the-page-with-the-data/index.html#the-info
	 * 
	 * INPUT: http://localhost:8080/research/page/12/wiki/2015/12/the-page-with-the-data/strange#the-info?fred=1
	 * OUTPUT: research/page/12/wiki/2015/12/the-page-with-the-data/strange#the-info?fred=1
	 * }
	 * </pre>
	 * 
	 * @param request a request for a webpage resource
	 * @return a resource path in the same format as that stored in the {@link #path} property
	 */
	private static String getResourcePath(HttpServletRequest request) throws UnsupportedEncodingException {
		final String contextPath = request.getServletContext().getContextPath();
		final String requestUri = URLDecoder.decode(request.getRequestURI(), URL_CHAR_ENCODING);
		int contextPathOffset = requestUri.indexOf(contextPath);
		String resourcePath = requestUri.substring(contextPathOffset + contextPath.length());
		// strip off the "/page/<id>/" suffix to the URL
		final int firstCharAfterSlashPageSlash = 6; // "/page/".length()
		int offset = resourcePath.indexOf('/', firstCharAfterSlashPageSlash) + 1;
		resourcePath = resourcePath.substring(offset);
		/*
		 * Reconstruct the parameters as they were originally provided in the URL since the file
		 * resource (and thus the stored resource path) will contain details of the parameters. The
		 * Map is a LinkedHashMap and should maintain the original order of the parameters
		 */
		final Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap.size() > 0) {

			final StringBuilder sb = new StringBuilder(resourcePath).append("?");
			boolean notFirstName = false;
			for (final String parameterName : parameterMap.keySet()) {
				if (notFirstName) {
					sb.append("&");
				} else {
					notFirstName = true;
				}
				sb.append(URLEncoder.encode(parameterName, URL_CHAR_ENCODING)).append("=");
				boolean notFirstValue = false;
				for (final String value : parameterMap.get(parameterName)) {
					if (notFirstValue) {
						sb.append("+");
					} else {
						notFirstValue = true;
					}
					sb.append(URLEncoder.encode(value, URL_CHAR_ENCODING));
				}
			}
			resourcePath = sb.toString();
		}
		return resourcePath;
	}
}
