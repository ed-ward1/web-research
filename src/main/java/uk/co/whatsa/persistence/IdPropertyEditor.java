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

    /**
     * {@inheritDoc}
     */
    @Override
	public final void setAsText(final String text) throws IllegalArgumentException {
		if (text == null || text.length() == 0) {
			setValue(null);
		} else {
			setValue(ID.valueOf(text));
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public final String getAsText() {
		final Object value = getValue();
		String result = "";
		if (value != null) {
		    result = ((ID) value).toString();
		}
		return result;
	}
}