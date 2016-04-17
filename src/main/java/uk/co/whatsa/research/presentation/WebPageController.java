package uk.co.whatsa.research.presentation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;

import javax.servlet.http.HttpServletRequest;

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
import uk.co.whatsa.research.ResearchRuntimeException;
import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.WebPageResource;
import uk.co.whatsa.research.model.WebPageVersion;

/**
 * This controller contains request handlers responsible for the
 * capture and display of website web pages.
 */
@Controller
@RequestMapping(path = "/page")
public class WebPageController {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(WebPageController.class);

    /** All URLs are encoded as ASCII. */
    private static final String URL_CHAR_ENCODING = "US-ASCII";

    /** Provides access to persistent instances. */
    @Autowired
    private ResearchDAO researchDAO;

    /**
     * Used to run the "capture web page" functionality
     * asynchronously.
     */
    @Autowired
    private ExecutorCompletionService<CaptureWebPageResult> completionService;

    /** Access to the Spring application context bean factory. */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * Handles the request to get the "capture web page" page (HTTP
     * GET). The method sets up the properties that are displayed in
     * the "capture web page" form, which currently means just the
     * "recorded date" value.
     * 
     * @param model the model presented to the views
     * @return the string "captureWebPage" which is the name of the
     *         .html Thymeleaf template. The value is picked up and
     *         "resolved" by the ThymeleafViewResolver (see web
     *         application context configuration).
     */
    @RequestMapping(path = "/capture", method = RequestMethod.GET)
    public final String getCaptureWebPage(final Model model) {
        model.addAttribute(new WebPageModelAttribute());
        return "captureWebPage";
    }

    /**
     * Handles the submission of the "capture web page" form data. The
     * method is responsible for initiating the process of downloading
     * the web page and persisting the details afterwards. The
     * download process is run in a separate Thread via an
     * {@link ExecutorCompletionService}.
     * 
     * @param parameters the parameters entered in the web page
     * @param bindingResult used to determine if there were errors
     *            binding the form strings to their associated page
     *            variables
     * @return Details of the download process are returned to the
     *         client via a {@link TailedFile} object.
     */
    @RequestMapping(path = "/captureAjax", method = RequestMethod.POST)
    public final @ResponseBody TailedFile postCaptureAjaxWebPage(final @ModelAttribute WebPageModelAttribute parameters,
            final BindingResult bindingResult) {
        final TailedFile tailedFile = new TailedFile();
        if (!bindingResult.hasErrors()) {
            final CaptureWebPage captureWebPage = beanFactory.getBean(CaptureWebPage.class, parameters);
            completionService.submit(captureWebPage);
            tailedFile.setSelector(captureWebPage.getRandomName());
            tailedFile.setFileName(captureWebPage.getOutputLogFilename());
        }

        return tailedFile;
    }

    /**
     * Returns the HTML page fragment for for the tab used to display
     * the tailed output resulting from a new page capture.
     * 
     * @param selector The id of the DOM element to be appended
     * @param model the model to receive the selector attribute
     * @return the string
     * 
     *         <pre>
     *         "fragments/pageTemplate :: tabFrag"
     *         </pre>
     * 
     *         which Thymeleaf interprets as an HTML fragment. See
     *         HTML page code:
     * 
     *         <pre>
     *         th:fragment="tabFrag"
     *         </pre>
     */
    @RequestMapping(path = "/newTab", method = RequestMethod.POST)
    public final String fragment(final @RequestParam String selector, final Model model) {
        model.addAttribute("selector", selector);
        return "fragments/pageTemplate :: tabFrag";
    }

    /**
     * A handy way to display a captured web page in the browser,
     * using just the web page's database id. The method redirects the
     * browser to the page by appending the page's resource path to
     * the request URL.
     * 
     * @param webPageVersionId the id of a WebPageVersion instance in
     *            the database
     * @param request the servlet request
     * @return a redirect URL path containing the page's resource
     *         path.
     */
    @RequestMapping(path = "{webPageVersionId}")
    public final String displayWebPage(final @PathVariable ID webPageVersionId, final HttpServletRequest request) {
        final WebPageVersion version = researchDAO.getWebPageVersion(webPageVersionId);
        final String view = new StringBuilder("redirect:/page/").append(webPageVersionId).append("/")
                .append(version.getMainResource().getPath()).toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Serving webpage for version id = " + webPageVersionId + ", view = " + view);
        }
        return view;
    }

    /**
     * Displays a captured web page (or web page resource) in the
     * browser.
     * 
     * @param webPageVersionId the database id of the web page
     * @param request the servlet request
     * @param output the {@code OutputStream} to write the web page
     *            resource to
     */
    @RequestMapping(path = "{webPageVersionId}/**")
    public final void displayWebPage(final @PathVariable ID webPageVersionId, final HttpServletRequest request,
            final OutputStream output) {
        byte[] resourceData = null;
        try {
            final String resourcePath = getResourcePath(request);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Serving webpage for version id = " + webPageVersionId + ", url = " + resourcePath);
            }
            final WebPageResource resource = researchDAO.getWebPageResource(webPageVersionId, resourcePath);
            if (resource != null) {
                resourceData = resource.getResourceData();
            }
        } catch (UnsupportedEncodingException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("no resource found for : " + webPageVersionId, e);
            }
        }

        if (resourceData == null) {
            resourceData = "<html></html>".getBytes();
        }

        try {
            output.write(resourceData);
        } catch (IOException e) {
            throw new ResearchRuntimeException(e);
        }
    }

    /**
     * Examples of input request and output string ...
     * 
     * <pre>
     *  {@code INPUT:
     * http://localhost:8080/research/page/12/wiki/2015/12/the-page-
     * with-the-data/ OUTPUT:
     * research/page/12/wiki/2015/12/the-page-with-the-data/index.html
     *
     * INPUT:
     * http://localhost:8080/research/page/12/wiki/2015/12/the-page-
     * with-the-data/#the-info OUTPUT:
     * research/page/12/wiki/2015/12/the-page-with-the-data/index.html
     * #the-info
     * 
     * INPUT:
     * http://localhost:8080/research/page/12/wiki/2015/12/the-page-
     * with-the-data/strange#the-info?fred=1 OUTPUT:
     * research/page/12/wiki/2015/12/the-page-with-the-data/strange#
     * the-info?fred=1 }
     * </pre>
     * 
     * @param request a request for a webpage resource
     * @return a resource path in the same format as that stored in
     *         the {@link #path} property
     * 
     * @throws UnsupportedEncodingException from
     *             {@link URLEncoder#encode(String, String)}
     */
    private static String getResourcePath(final HttpServletRequest request) throws UnsupportedEncodingException {
        final String contextPath = request.getServletContext().getContextPath();
        final String requestUri = URLDecoder.decode(request.getRequestURI(), URL_CHAR_ENCODING);
        final int contextPathOffset = requestUri.indexOf(contextPath);
        String resourcePath = requestUri.substring(contextPathOffset + contextPath.length());
        // strip off the "/page/<id>/" suffix to the URL
        final int slashPageSlashLen = 6; // "/page/".length()
        final int offset = resourcePath.indexOf('/', slashPageSlashLen) + 1;
        resourcePath = resourcePath.substring(offset);
        /*
         * Reconstruct the parameters as they were originally provided
         * in the URL since the file resource (and thus the stored
         * resource path) will contain details of the parameters. The
         * Map is a LinkedHashMap and should maintain the original
         * order of the parameters
         */
        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.isEmpty()) {

            final StringBuilder sb = new StringBuilder(resourcePath).append("?");
            boolean notFirstName = false;
            for (final String parameterName : parameterMap.keySet()) {
                if (notFirstName) {
                    sb.append('&');
                } else {
                    notFirstName = true;
                }
                sb.append(URLEncoder.encode(parameterName, URL_CHAR_ENCODING));
                boolean notFirstValue = false;
                for (final String value : parameterMap.get(parameterName)) {
                    if (value.length() == 0) {
                        continue;
                    }
                    if (notFirstValue) {
                        sb.append('+');
                    } else {
                        notFirstValue = true;
                        sb.append('=');
                    }
                    sb.append(URLEncoder.encode(value, URL_CHAR_ENCODING));
                }
            }
            resourcePath = sb.toString();
        }
        return resourcePath;
    }
}
