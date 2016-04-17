package uk.co.whatsa.research.model;

/**
 * Represents a web page CSS resource.
 */
public class CssWebPageResource extends TextWebPageResource {

    /**
     * Default constructor.
     */
    public CssWebPageResource() {
	    super();
	}

    /**
     * @param mimeContentType {@link TextWebPageResource#setMimeContentType()}
     */
	public CssWebPageResource(final String mimeContentType) {
		super(mimeContentType);
	}
}
