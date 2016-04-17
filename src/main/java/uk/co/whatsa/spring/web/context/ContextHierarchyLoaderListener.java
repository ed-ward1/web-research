package uk.co.whatsa.spring.web.context;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Context loader class that loads a parent context, using <code>SingletonBeanFactoryLocator</code>.
 * Uses <code>SingletonBeanFactoryLocator</code> default definition (beanRefFactory.xml) file as the
 * default parent context definition file. It is possible to override this definition by adding the
 * <i>parentContextConfigLocation</i> context-param in the web.xml file.
 * 
 * @author E. Hardy
 * @version $Revision: $ $Date: $
 */
public class ContextHierarchyLoaderListener extends ContextLoaderListener {
	/**
	 * Name of servlet context parameter that can specify the parent config location for the root
	 * context, falling back to the implementation's default else (beanRefFactory.xml).
	 * 
	 * <p>
	 * This constant value is: parentContextConfigLocation
	 */
	public static final String PARENT_CONFIG_LOCATION_PARAM = "parentContextConfigLocation";

	/**
	 * Name of servlet context parameter that can specify the parent bean factory name for the root
	 * context, falling back to the implementation's default else (parentBeanFactory).
	 * 
	 * <p>
	 * This constant value is: parentBeanFactoryName
	 */
	public static final String BEAN_FACTORY_NAME_PARAM = "parentBeanFactoryName";

	/** The name of the default bean factory. */
	private static final String DEFAULT_BEAN_FACTORY_NAME = "parentBeanFactory";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final ApplicationContext loadParentContext(final ServletContext servletContext) {
		String parentContextConfig = servletContext.getInitParameter(PARENT_CONFIG_LOCATION_PARAM);
		BeanFactoryLocator locator = null;

		if ((parentContextConfig != null) && (parentContextConfig.trim().length() > 0)) {
			locator = SingletonBeanFactoryLocator.getInstance(parentContextConfig);
		} else {
			locator = SingletonBeanFactoryLocator.getInstance();
		}

		String beanFactoryName = servletContext.getInitParameter(BEAN_FACTORY_NAME_PARAM);
		BeanFactoryReference bfr = null;

		if ((beanFactoryName != null) && (beanFactoryName.trim().length() > 0)) {
			bfr = locator.useBeanFactory(beanFactoryName);
		} else {
			bfr = locator.useBeanFactory(DEFAULT_BEAN_FACTORY_NAME);
		}

		return (ApplicationContext) bfr.getFactory();
	}
}
