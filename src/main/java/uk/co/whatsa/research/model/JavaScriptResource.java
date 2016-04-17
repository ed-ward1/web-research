package uk.co.whatsa.research.model;

import uk.co.whatsa.persistence.Persistent;

/**
 * Represents a shared JavaScript resource and encapsulates a row persisted in the
 * JAVASCRIPT_RESOURCE table.
 * 
 * The table has a one-to-many relationship with the WEBPAGE_RESOURCE table. JavaScript resources
 * can be very large and is often common between web pages (such as the JQuery library). Sharing
 * JavaScript resources reduces that amount of data stored for each page.
 */
public class JavaScriptResource extends Persistent {
    /** The name of the resource. */
	private String name;
	
	/** The JavaScript text. */
	private String javaScript;

	/**
	 * @return {@link #name}
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name {@link #name}
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return {@link #javaScript}
	 */
	public final String getJavaScript() {
		return javaScript;
	}

	/**
	 * @param javaScript {@link #javaScript}
	 */
	public final void setJavaScript(final String javaScript) {
		this.javaScript = javaScript;
	}
}
