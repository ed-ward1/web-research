package uk.co.whatsa.research.dao.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.co.whatsa.persistence.ID;
import uk.co.whatsa.research.dao.ResearchDAO;
import uk.co.whatsa.research.model.JavaScriptResource;
import uk.co.whatsa.research.model.WebPage;
import uk.co.whatsa.research.model.WebPageResource;
import uk.co.whatsa.research.model.WebPageVersion;

/**
 * A Hibernate ORM implementation of the ResearchDAO interface.
 */
public class ResearchDaoHibernate implements ResearchDAO {
    /** A logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ResearchDaoHibernate.class);

    /**
     * The HQL query string used to retrieve a {@link WebPageResource}
     * .
     */
    private static final String GET_WEB_PAGE_RESOURCE = "from WebPageResource wpr "
            + "join wpr.webPageVersions version where version.id = :webPageVersionId and wpr.path like :path";

    /**
     * The HQL query string used to retrieve a
     * {@link JavaScriptResource} with a given name.
     */
    private static final String FIND_JAVASCRIPT_RESOURCE_WITH_NAME =
            "from JavaScriptResource jsr where jsr.name = :name";

    /** The Hibernate session factory used to query the database. */
    private SessionFactory sessionFactory;

    /**
     * @param sessionFactory {@link #sessionFactory}
     */
    public final void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public final WebPage getWebPage(final ID webPageId) {
        final Session session = sessionFactory.getCurrentSession();
        return session.get(WebPage.class, webPageId, LockMode.NONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public final WebPageVersion getWebPageVersion(final ID webPageVersionId) {
        final Session session = sessionFactory.getCurrentSession();
        return session.get(WebPageVersion.class, webPageVersionId, LockMode.NONE);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public final List<WebPage> getWebPages() {
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery("from WebPage");
        List<WebPage> webPages;
        try {
            webPages = query.list();
        } catch (ObjectNotFoundException e) {
            webPages = new ArrayList<WebPage>(0);
        }
        return webPages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = RuntimeException.class)
    public final void saveWebPage(final WebPage webPage) {
        final Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(webPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public final WebPageResource getWebPageResource(final ID webPageVersionId, final String resourcePath) {
        final Session session = sessionFactory.getCurrentSession();

        final Query query = session.createQuery(GET_WEB_PAGE_RESOURCE)
                .setInteger("webPageVersionId", webPageVersionId.getIntValue()).setString("path", resourcePath);
        @SuppressWarnings("rawtypes")
        final Iterator iterator = query.iterate();
        WebPageResource webPageResource = null;
        if (iterator.hasNext()) {
            // The iterator will return an Object[] containing a
            // WebPageResource and a WebPage (the joined objects)
            final Object[] objects = (Object[]) query.iterate().next();
            webPageResource = (WebPageResource) objects[0];
            // We read the data here while the transaction is active.
            // This is an abstract method and cannot
            // easily be specified in the HQL
            webPageResource.getResourceData();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No resource found - WebPage id: " + webPageVersionId + ", " + resourcePath);
            }
        }

        return webPageResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public final JavaScriptResource findJavaScriptResourceWithName(final String name) {
        final Session session = sessionFactory.getCurrentSession();

        return (JavaScriptResource) session.createQuery(FIND_JAVASCRIPT_RESOURCE_WITH_NAME).setString("name", name)
                .uniqueResult();
    }
}
