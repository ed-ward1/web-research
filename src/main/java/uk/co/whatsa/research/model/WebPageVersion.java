package uk.co.whatsa.research.model;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import uk.co.whatsa.persistence.Persistent;

/**
 * Represents a web page. The page encapsulates child
 * {@link WebPageResource} instances that comprise the web page.
 * Instances of the class are persisted to the WEBPAGE table.
 */
public class WebPageVersion extends Persistent {

    ////////////////////////////////////
    // The persistent state of a WebPage

    /** The date the page was recorded / saved. */
    private DateTime recordedDate;

    /**
     * The root HTML resource that was "saved" and which is displayed.
     */
    private WebPageResource mainResource;

    /** The page that manages this version. */
    private WebPage owningWebPage;

    /**
     * A collection of all the "saved" resources, including the "main"
     * one associated with / comprising this page.
     */
    private Set<WebPageResource> resources = new HashSet<WebPageResource>();

    // The persistent state of a WebPage
    ////////////////////////////////////

    /**
     * @return {@link #recordedDate}
     */
    public final DateTime getRecordedDate() {
        return recordedDate;
    }

    /**
     * @param recordedDate {@link #recordedDate}
     */
    public final void setRecordedDate(final DateTime recordedDate) {
        this.recordedDate = recordedDate;
    }

    /**
     * @return {@link #owningWebPage}
     */
    public final WebPage getOwningWebPage() {
        return owningWebPage;
    }

    /**
     * @param owningWebPage {@link #owningWebPage}
     */
    public final void setOwningWebPage(final WebPage owningWebPage) {
        this.owningWebPage = owningWebPage;
    }

    /**
     * @return {@link #mainResource}
     */
    public final WebPageResource getMainResource() {
        return mainResource;
    }

    /**
     * @param mainResource {@link #mainResource}
     */
    public final void setMainResource(final WebPageResource mainResource) {
        this.mainResource = mainResource;
    }

    /**
     * @return the collection of {@link WebPageResource} instances
     *         associated with this {@link WebPageVersion} instance
     */
    public final Set<WebPageResource> getResources() {
        return resources;
    }

    /**
     * @param resources {@link #resources}
     */
    public final void setResources(final Set<WebPageResource> resources) {
        this.resources = resources;
    }

    ///////////////////////////////////////////////////////////////////////
    // Behaviour and functionality in addition to the persistent
    /////////////////////////////////////////////////////////////////////// properties.

    /**
     * Associates a @link {@link WebPageResource} with this web page.
     * 
     * @param resource the resource to associate. The object is not
     *            cloned before being added to the internal
     *            collection.
     */
    public final void addResource(final WebPageResource resource) {
        getResources().add(resource);
        resource.addWebPageVersion(this);
    }

    /**
     * @return the URL used to retrieve this web page
     */
    public final String getUrl() {
        return owningWebPage.getUrl();
    }
}
