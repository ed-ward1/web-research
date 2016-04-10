package uk.co.whatsa.research.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.whatsa.research.model.BlobWebPageResource;
import uk.co.whatsa.research.model.CssWebPageResource;
import uk.co.whatsa.research.model.HtmlWebPageResource;
import uk.co.whatsa.research.model.ImageWebPageResource;
import uk.co.whatsa.research.model.JsWebPageResource;
import uk.co.whatsa.research.model.TextWebPageResource;
import uk.co.whatsa.research.model.WebPageResource;
import uk.co.whatsa.research.model.XmlWebPageResource;

public enum WebPageResourceType {
	HTML(HtmlWebPageResource.class), JS(JsWebPageResource.class), CSS(CssWebPageResource.class), IMAGE(
			ImageWebPageResource.class), BLOB(BlobWebPageResource.class), TEXT(TextWebPageResource.class), XML(
					XmlWebPageResource.class);

	private static final Logger LOG = LoggerFactory.getLogger(WebPageResource.class);
	
	private Class<? extends WebPageResource> resourceClass;

	private WebPageResourceType(Class<? extends WebPageResource> resourceClass) {
		this.resourceClass = resourceClass;
	}

	public WebPageResource createResource() {
		try {
			return (WebPageResource) resourceClass.newInstance();
		} catch (Exception e) {
			LOG.error("failed to instantiate instance of ", resourceClass);
		}
		return null;
	}

	public static WebPageResourceType getWebPageResourceType(String mimeContentType) {
		WebPageResourceType resourceType = null;
		if ("application/javascript".equals(mimeContentType)) {
			resourceType = JS;
		} else if ("text/css".equals(mimeContentType)) {
			resourceType = CSS;
		} else if ("application/xml".equals(mimeContentType) || mimeContentType.endsWith("+xml")) {
			resourceType = XML;
		} else if (mimeContentType.startsWith("image/")) {
			resourceType = IMAGE;
		} else if ("text/html".equals(mimeContentType)) {
			resourceType = HTML;
		} else if (mimeContentType.startsWith("text/")) {
			resourceType = TEXT;
		} else {
			LOG.warn("unexpected mime content type", mimeContentType);
			resourceType = BLOB;
		}
		return resourceType;
	}
}
