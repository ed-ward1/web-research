package uk.co.whatsa.research.model;

public class ImageWebPageResource extends BlobWebPageResource {

	public ImageWebPageResource() {
	}

	public ImageWebPageResource(String mimeContentType) {
		super(mimeContentType);
	}

	public byte[] getResourceImage() {
		return super.getResourceBlob();
	}

	public void setResourceImage(byte[] image) {
		super.setResourceBlob(image);
	}
}
