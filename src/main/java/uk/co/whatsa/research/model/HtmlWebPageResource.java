package uk.co.whatsa.research.model;

/**
 * Represents an HTML web page resource.
 */
public class HtmlWebPageResource extends TextWebPageResource {

    /**
     * Default constructor.
     */
    public HtmlWebPageResource() {
	    super();
	}

    /**
     * @param mimeContentType {@link TextWebPageResource#setMimeContentType(String)}
     */
	public HtmlWebPageResource(final String mimeContentType) {
		super(mimeContentType);
	}
}
