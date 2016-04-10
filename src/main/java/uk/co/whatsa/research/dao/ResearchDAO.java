package uk.co.whatsa.research.dao;

import java.util.List;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.research.model.JavaScriptResource;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;

public interface ResearchDAO {

	WebPage getWebPage(ID webPageId);

	List<WebPage> getWebPages();
	
	void saveWebPage(WebPage webPage);
	
	WebPageResource getWebPageResource(ID webPageId, String resourceUrl);
	
	JavaScriptResource findJavaScriptResourceWithName(String name);
}
