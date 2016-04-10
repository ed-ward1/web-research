package uk.co.whatsa.research.test;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.research.model.WebPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:/researchRootContext.xml")
public class WebPageTest {
	@Autowired
	private SessionFactory sessionFactory;
	private Session session;

	@Before
	public void onStart() {
		this.session = sessionFactory.getCurrentSession();
	}

	@Test
	@Transactional(readOnly=true)	
	public void readWebPages() {
		WebPage webPage = session.get(WebPage.class, ID.valueOf(1));
		assertEquals("First test page", webPage.getComment());
	}
}
