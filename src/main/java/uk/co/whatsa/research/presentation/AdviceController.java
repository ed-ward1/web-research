package uk.co.whatsa.research.presentation;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.persistence.IdPropertyEditor;

/**
 * Definitions and code that is common to all controllers in the application.
 */
@ControllerAdvice
public class AdviceController {
	
	/**
	 * Registers a custom editor used to convert to/from ID values passed by the browser.
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	binder.registerCustomEditor(ID.class, new IdPropertyEditor());
    }

}
