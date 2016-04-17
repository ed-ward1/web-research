package uk.co.whatsa.research.dao;

import java.util.List;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.research.model.JavaScriptResource;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;
import uk.co.whatsa.research.model.WebPageVersion;

/**
 * A simple DAO interface for accessing and persisting web page
 * resource data.
 */
public interface ResearchDAO {

    /**
     * Retrieves a {@link WebPage} instance from persistent store.
     * 
     * @param webPageId the persistent identifier of the web page to
     *            retrieve
     * @return the web page or {@code null} if there was no page with
     *         the given identifier
     */
    WebPage getWebPage(ID webPageId);

    /**
     * Retrieves a {@link WebPageVersion} instance from persistent
     * store.
     * 
     * @param webPageVersionId the persistent identifier of the web
     *            page version to retrieve
     * @return the web page version of {@code null} if there was no
     *         web page version with the given identifier
     */
    WebPageVersion getWebPageVersion(ID webPageVersionId);

    /**
     * TODO this method needs to be removed. It is only present for
     * testing purposes.
     * 
     * @return a collection of all the available web pages
     */
    List<WebPage> getWebPages();

    /**
     * Persists the provided web page instance.
     * 
     * @param webPage the web page to make persistent
     */
    void saveWebPage(WebPage webPage);

    /**
     * Retrieves a {link {@link WebPageResource} instance from
     * persistent store.
     * 
     * @param webPageVersionId the web page version the resource is
     *            associated with
     * @param resourceUrl the URL of the resource to be retrieved
     * @return the {@code WebPageResource} or {@code null} if no
     *         object was available that is associated with the given
     *         web page version and resource URL.
     */
    WebPageResource getWebPageResource(ID webPageVersionId, String resourceUrl);

    /**
     * @param name the name of the JavaScript resource to retrieve
     * @return a {@link JavaScriptResource} instance corresponding to
     *         the given name or {code null} if no resource was
     *         available
     */
    JavaScriptResource findJavaScriptResourceWithName(String name);
}
