package uk.co.whatsa.research.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.whatsa.research.model.BlobWebPageResource;
import uk.co.whatsa.research.model.CssWebPageResource;
import uk.co.whatsa.research.model.HtmlWebPageResource;
import uk.co.whatsa.research.model.ImageWebPageResource;
import uk.co.whatsa.research.model.JsWebPageResource;
import uk.co.whatsa.research.model.TextWebPageResource;
import uk.co.whatsa.research.model.WebPageResource;
import uk.co.whatsa.research.model.XmlWebPageResource;

/**
 * Represents the resource type for a web page. The resource type is
 * used to determine the descriminator "type" field value in the
 * WEBPAGE_RESOURCE table.
 */
public enum WebPageResourceType {
    /** An {@link HtmlWebPageResource}. */
    HTML(HtmlWebPageResource.class),
    /** A {@link JsWebPageResource}. */
    JS(JsWebPageResource.class),
    /** A {@link CssWebPageResource}. */
    CSS(CssWebPageResource.class),
    /** A {@link ImageWebPageResource}. */
    IMAGE(ImageWebPageResource.class),
    /** A {@link BlobWebPageResource}. */
    BLOB(BlobWebPageResource.class),
    /** A {@link TextWebPageResource}. */
    TEXT(TextWebPageResource.class),
    /** A {@link XmlWebPageResource}. */
    XML(XmlWebPageResource.class);

    /** A Logger used to log error and debug messages. */
    private static final Logger LOG = LoggerFactory.getLogger(WebPageResource.class);

    /** The class associated with the type. */
    private Class<? extends WebPageResource> resourceClass;

    /**
     * Constructor.
     * 
     * @param resourceClass the type of the resource associated with
     *            an individual enum instance.
     */
    WebPageResourceType(final Class<? extends WebPageResource> resourceClass) {
        this.resourceClass = resourceClass;
    }

    /**
     * @return an instance of WebPageResource. The type constructed
     *         depends on the enum object.
     */
    public final WebPageResource createResource() {
        WebPageResource resource = null;
        Exception failed = null;
        try {
            resource = (WebPageResource) resourceClass.newInstance();
        } catch (InstantiationException e) {
            failed = e;
        } catch (IllegalAccessException e) {
            failed = e;
        }

        if (failed != null && LOG.isErrorEnabled()) {
            LOG.error("failed to instantiate instance of " + resourceClass.getName(), failed);
        }

        return resource;
    }

    /**
     * @param mimeContentType a mime type string. eg. "text/html"
     * @return an enum instance of type {@link WebPageResourceType}
     */
    public static WebPageResourceType getWebPageResourceType(final String mimeContentType) {
        WebPageResourceType resourceType = null;
        if ("application/javascript".equals(mimeContentType)) {
            resourceType = JS;
        } else if ("text/css".equals(mimeContentType)) {
            resourceType = CSS;
        } else if ("application/xml".equals(mimeContentType) || mimeContentType.endsWith("+xml")) {
            resourceType = XML;
        } else if (mimeContentType.startsWith("image/")) {
            resourceType = IMAGE;
        } else if ("text/html".equals(mimeContentType)) {
            resourceType = HTML;
        } else if (mimeContentType.startsWith("text/")) {
            resourceType = TEXT;
        } else {
            LOG.warn("unexpected mime content type", mimeContentType);
            resourceType = BLOB;
        }
        return resourceType;
    }
}
