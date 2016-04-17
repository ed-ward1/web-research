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

/**
 * Tests the {@link WebPage} domain model class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:/researchRootContext.xml")
public class WebPageTest {
    /**
     * The Hibernate session factory.
     */
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * The Hibernate session. 
	 */
	private Session session;

	/**
	 * Test initialisation.
	 */
	@Before
	public final void onStart() {
		this.session = sessionFactory.getCurrentSession();
	}

	/**
	 * Read some pages.
	 */
	@Test
	@Transactional(readOnly = true)	
	public final void readWebPages() {
		final WebPage webPage = session.get(WebPage.class, ID.valueOf(1));
		assertEquals("First test page", webPage.getUrl());
	}
}
