package uk.co.whatsa.research.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.JavaScriptResource;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;

public class ResearchDaoHibernate implements ResearchDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional(readOnly = true)
	public WebPage getWebPage(ID webPageId) {
		Session session = sessionFactory.getCurrentSession();
		final WebPage webPage = session.get(WebPage.class, webPageId, LockMode.NONE);
		return webPage;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<WebPage> getWebPages() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from WebPage");
		List<WebPage> webPages;
		try {
			webPages = query.list();
		} catch (ObjectNotFoundException e) {
			webPages = new ArrayList<WebPage>(0);
		}
		return webPages;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = RuntimeException.class)
	public void saveWebPage(WebPage webPage) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(webPage);
	}

	@Override
	@Transactional(readOnly = true)
	public WebPageResource getWebPageResource(ID webPageId, String resourcePath) {
		final Session session = sessionFactory.getCurrentSession();

		final String queryString = "from WebPageResource wpr where wpr.webPage.id = :webPageId and wpr.path like :path";

		final WebPageResource webPageResource = (WebPageResource) session.createQuery(queryString)
				.setInteger("webPageId", webPageId.getIntValue()).setString("path", resourcePath).uniqueResult();

		return webPageResource;
	}

	@Override
	@Transactional(readOnly = true)
	public JavaScriptResource findJavaScriptResourceWithName(String name) {
		final Session session = sessionFactory.getCurrentSession();

		final String queryString = "from JavaScriptResource jsr where jsr.name = :name";

		JavaScriptResource javaScriptResource = (JavaScriptResource) session.createQuery(queryString)
				.setString("name", name).uniqueResult();

		return javaScriptResource;
	}
}
