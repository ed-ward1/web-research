package uk.co.whatsa.research.model;

public class TextWebPageResource extends WebPageResource {
	private String resourceText;

	public TextWebPageResource() {
	}

	public TextWebPageResource(String mimeContentType) {
		super(mimeContentType);
	}

	public String getResourceText() {
		return resourceText;
	}

	public void setResourceText(String resourceText) {
		this.resourceText = resourceText;
	}

	@Override
	public byte[] getResourceData() {
		return resourceText.getBytes();
	}

	@Override
	public void setResourceData(byte[] resourceData) {
		setResourceText(new String(resourceData));
	}
}
