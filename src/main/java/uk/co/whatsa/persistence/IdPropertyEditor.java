package uk.co.whatsa.persistence;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;

/**
 * This {@code PropertyEditor} is used to convert "id" parameter strings sent in the web request
 * into {@link ID} types that are used by the persistent types. The class is also responsible for
 * converting ID types into strings that are sent back to the client.
 */
@Component
public class IdPropertyEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || "".equals(text)) {
			setValue(null);
		} else {
			setValue(ID.valueOf(text));
		}
	}

	@Override
	public String getAsText() {
		final Object value = getValue();
		return value == null ? "" : ((ID) value).toString();
	}
}