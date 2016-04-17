package uk.co.whatsa.research.model;

import java.util.HashSet;
import java.util.Set;

import uk.co.whatsa.persistence.Persistent;

/**
 * A logical representation of a web page. The class holds a
 * collection of {@link WebPage} instances, one for each time the page
 * was saved.
 */
public class WebPage extends Persistent {
    /** The internet URL of the page that was saved. */
    private String url;

    /** Any comments associated with the page. */
    private String comment;

    /** An instance of WebPage for each time the page was saved. */
    private Set<WebPageVersion> versions = new HashSet<WebPageVersion>();

    /**
     * Default constructor.
     */
    public WebPage() {
        super();
    }

    /**
     * @param url the URL used to retrieve the web page
     * @param comment a use comment associated with the web page
     */
    public WebPage(final String url, final String comment) {
        super();
        this.url = url;
        this.comment = comment;
    }

    /**
     * @return {@link #url}
     */
    public final String getUrl() {
        return url;
    }

    /**
     * @param url {@link #url}
     */
    public final void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return {@link #comment}
     */
    public final String getComment() {
        return comment;
    }

    /**
     * @param comment {@link #comment}
     */
    public final void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * @return {@link #versions}
     */
    public final Set<WebPageVersion> getVersions() {
        return versions;
    }

    /**
     * @param versions {@link #versions}
     */
    public final void setVersions(final Set<WebPageVersion> versions) {
        this.versions = versions;
    }

    /**
     * Adds a web page version to the collection managed by the web
     * page. The web page should be associated with the URL contained
     * in this web page instance.
     * 
     * @param version the web page version to be associated with this
     *            web page
     * @return true if the addition succeeded
     */
    public final boolean addVersion(final WebPageVersion version) {
        final boolean result = versions.add(version);
        if (result) {
            version.setOwningWebPage(this);
        }
        return result;
    }

    /**
     * Removes a web page version from the collection managed by this
     * web page.
     * 
     * @param version the version to be removed
     * @return {@code true} if the removal succeeded else
     *         {@code false} is returned
     */
    public final boolean removeVersion(final WebPageVersion version) {
        boolean result = false;
        if (version.getOwningWebPage().equals(this)) {
            result = versions.remove(version);
            version.setOwningWebPage(null);
        }
        return result;
    }

    /**
     * TODO return the latest version rather than any old version.
     * 
     * @return The most recently added version of the web page
     */
    public final WebPageVersion getNewestVersion() {
        return versions.iterator().next();
    }
}
