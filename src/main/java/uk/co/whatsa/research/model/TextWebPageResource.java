package uk.co.whatsa.research.model;

/**
 * Represents a simple text file web page resource. The class also acts as a base class for
 * other resource types, such as {@link HtmlWebPageResource} and {@link CssWebPageResource}.
 */
public class TextWebPageResource extends WebPageResource {
    
    /** The resource text. */ 
	private String resourceText;

	/**
	 * Default constructor.
	 */
	public TextWebPageResource() {
	    super();
	}

	/**
	 * @param mimeContentType {@link WebPageResource#setMimeContentType(String)}
	 */
	public TextWebPageResource(final String mimeContentType) {
		super(mimeContentType);
	}

	/**
	 * @return {@link #resourceText}
	 */
	public final String getResourceText() {
		return resourceText;
	}

	/**
	 * @param resourceText {@link #resourceText}
	 */
	public final void setResourceText(final String resourceText) {
		this.resourceText = resourceText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final byte[] getResourceData() {
		return resourceText.getBytes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setResourceData(final byte[] resourceData) {
		setResourceText(new String(resourceData));
	}
}
