package uk.co.whatsa.research.model;

/**
 * Represents an image web page resource.
 */
public class ImageWebPageResource extends BlobWebPageResource {

    /**
     * Default constructor.
     */
    public ImageWebPageResource() {
	    super();
	}

    /**
     * @param mimeContentType {@link BlobWebPageResource#setMimeContentType(String)}
     */
    public ImageWebPageResource(final String mimeContentType) {
		super(mimeContentType);
	}

	/**
	 * @return the image data associate with the resource.
	 */
	public final byte[] getResourceImage() {
		return super.getResourceBlob();
	}

	/**
	 * @param image the image data to associate with this resource
	 */
	public final void setResourceImage(final byte[] image) {
		super.setResourceBlob(image.clone());
	}
}
