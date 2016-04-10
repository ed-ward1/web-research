package uk.co.whatsa.research.model;

import uk.co.whatsa.persistence.Persistent;

/**
 * The base class for persistent resources that constitute a web page.
 */
public abstract class WebPageResource extends Persistent {
	private WebPage webPage;
	private String path;
	private String mimeContentType;

	public WebPageResource() {
	}

	public WebPageResource(String mimeContentType) {
		this.mimeContentType = mimeContentType;
	}
	
	public WebPage getWebPage() {
		return webPage;
	}

	public void setWebPage(WebPage webPage) {
		this.webPage = webPage;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMimeContentType() {
		return mimeContentType;
	}

	public void setMimeContentType(String mimeContentType) {
		this.mimeContentType = mimeContentType;
	}

	/** The data that is streamed to the browser in response to the request for the resource. */
	public abstract byte[] getResourceData();

	/** The data as read from the wget-created file in the form of a byte array. */
	public abstract void setResourceData(byte[] resourceData);
}
