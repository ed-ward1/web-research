package uk.co.whatsa.research.model;

import java.util.HashSet;
import java.util.Set;

import uk.co.whatsa.persistence.Persistent;

/**
 * The base class for persistent resources that constitute a web page.
 */
public abstract class WebPageResource extends Persistent {
    /**
     * A collection of web page versions associated with this
     * resource.
     */
    private Set<WebPageVersion> webPageVersions = new HashSet<WebPageVersion>();

    /** The URL-like path associated with this resource. */
    private String path;

    /** The mime type of the resource. */
    private String mimeContentType;

    /**
     * Default constructor.
     */
    public WebPageResource() {
        super();
    }

    /**
     * @param mimeContentType {@link #mimeContentType}
     */
    public WebPageResource(final String mimeContentType) {
        super();
        this.mimeContentType = mimeContentType;
    }

    /**
     * @return {@link #webPageVersions}
     */
    public final Set<WebPageVersion> getWebPageVersions() {
        return webPageVersions;
    }

    /**
     * @param webPageVersions {@link #webPageVersions}
     */
    public final void setWebPageVersions(final Set<WebPageVersion> webPageVersions) {
        this.webPageVersions = webPageVersions;
    }

    /**
     * @return {@link #path}
     */
    public final String getPath() {
        return path;
    }

    /**
     * @param path {@link #path}
     */
    public final void setPath(final String path) {
        this.path = path;
    }

    /**
     * @return {@link #mimeContentType}
     */
    public final String getMimeContentType() {
        return mimeContentType;
    }

    /**
     * @param mimeContentType {@link #mimeContentType}
     */
    public final void setMimeContentType(final String mimeContentType) {
        this.mimeContentType = mimeContentType;
    }

    /**
     * @return the data that is streamed to the browser in response to the
     * request for the resource.
     */
    public abstract byte[] getResourceData();

    /**
     * The data as read from the wget-created file in the form of a
     * byte array.
     * 
     * @param resourceData the data to associate with this resource
     */
    public abstract void setResourceData(byte[] resourceData);

    /**
     * @param webPageVersion the web page version to associate with
     *            this web page
     */
    public final void addWebPageVersion(final WebPageVersion webPageVersion) {
        webPageVersions.add(webPageVersion);
    }
}
