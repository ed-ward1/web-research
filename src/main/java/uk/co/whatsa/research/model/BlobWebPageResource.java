package uk.co.whatsa.research.model;

public class BlobWebPageResource extends WebPageResource {
	private byte[] blob;

	public BlobWebPageResource() {
	}

	public BlobWebPageResource(String mimeContentType) {
		super(mimeContentType);
	}

	public byte[] getResourceBlob() {
		return blob;
	}

	public void setResourceBlob(byte[] blob) {
		this.blob = blob;
	}

	@Override
	public byte[] getResourceData() {
		return blob;
	}

	@Override
	public void setResourceData(byte[] resourceData) {
		this.blob = resourceData;
	}
}
