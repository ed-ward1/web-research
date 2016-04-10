package uk.co.whatsa.research.model;

import uk.co.whatsa.persistence.Persistent;

public class JavaScriptResource extends Persistent {
	private String name;
	private String javaScript;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavaScript() {
		return javaScript;
	}

	public void setJavaScript(String javaScript) {
		this.javaScript = javaScript;
	}

}
