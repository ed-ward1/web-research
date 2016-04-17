package uk.co.whatsa.research.model;

/**
 * Represents a JavaScript web page resource. The class encapsulates a
 * reference to a shared JavaScript resource.
 */
public class JsWebPageResource extends WebPageResource {

    /**
     * The JavaScript resource object representing a row in the
     * database.
     */
    private JavaScriptResource javaScript;

    /**
     * Default constructor.
     */
    public JsWebPageResource() {
        super();
    }

    /**
     * @param mimeContentType {@link WebPageResource#setMimeContentType(String)}
     */
    public JsWebPageResource(final String mimeContentType) {
        super(mimeContentType);
    }

    /**
     * @return {@link #javaScript}
     */
    public final JavaScriptResource getJavaScript() {
        return javaScript;
    }

    /**
     * @param javaScript {@link #javaScript}
     */
    public final void setJavaScript(final JavaScriptResource javaScript) {
        this.javaScript = javaScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getResourceData() {
        return javaScript.getJavaScript().getBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setResourceData(final byte[] resourceData) {
        getJavaScript().setJavaScript(new String(resourceData));
    }
}
