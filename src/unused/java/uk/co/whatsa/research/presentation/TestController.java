package uk.co.whatsa.research.presentation;

import java.io.Writer;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.WebPage;

public class TestController {
	private static int counter;

	@Autowired
	private ResearchDAO researchDAO;
	
	@Autowired
	private WebPageController webPageController;
	
	@RequestMapping(path="/")
	public String welcome() {
		return "forward:/index";
	}

	@RequestMapping(path="/index")
	public void index(Model model) {
		model.addAttribute("counter", counter);
	}
	
	@RequestMapping(path="/render1")
	public String render1(Model model) {
		counter++;
		model.addAttribute("counter", counter);		
		return "index :: content1";
	}

	@RequestMapping(path="/render2")
	public String render2(Model model) {
		counter++;
		model.addAttribute("counter", counter);
		return "index :: content2";
	}

	@RequestMapping(path = "test")
	public void test(Writer writer) throws Exception {
		WebPage webPage = new WebPage();
		webPage.setComment("comment");
		webPage.setPageDate(DateTime.now());
		webPage.setRecordedDate(DateTime.now());
		webPage.setUrl("http://www.davidicke.com/headlines/");
//		researchDAO.saveWebPage(webPageController.retrieveWebPage(webPage));
		String url = "http://localhost:8080/research/page/" + webPage.getId();

		writer.write("<a href=\"" + url + "\">" + url + "</a>");
	}
	
}
