package uk.co.whatsa.research.model;

public class JsWebPageResource extends WebPageResource {
	private JavaScriptResource javaScript;
	
	public JsWebPageResource() {
	}

	public JsWebPageResource(String mimeContentType) {
		super(mimeContentType);
	}

	public JavaScriptResource getJavaScript() {
		return javaScript;
	}

	public void setJavaScript(JavaScriptResource javaScript) {
		this.javaScript = javaScript;
	}

	@Override
	public byte[] getResourceData() {
		return javaScript.getJavaScript().getBytes();
	}

	@Override
	public void setResourceData(byte[] resourceData) {
		getJavaScript().setJavaScript(new String(resourceData));
	}
}
