package uk.co.whatsa.research.model;

/**
 * Represents a web page XML resource.
 */
public class XmlWebPageResource extends TextWebPageResource {

    /**
     * Default constructor.
     */
	public XmlWebPageResource() {
	    super();
	}

	/**
	 * @param mimeContentType {@link TextWebPageResource#setMimeContentType(String)}
	 */
	public XmlWebPageResource(final String mimeContentType) {
		super(mimeContentType);
	}
}
