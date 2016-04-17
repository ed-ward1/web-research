package uk.co.whatsa.spring.web.context;

import org.springframework.web.context.support.XmlWebApplicationContext;

// Notes:The interface WebApplicationContext defines
// ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE WebApplicationContextUtils helper
// class can be used to retrieve the root web applicationcontext. The
// XmlWebApplicationContext class defines the default config location as
// WEB-INF/applicationContext.xml. If a ContextLoaderListener class is
// configured as a listener in the web.xml then the ContextLoader will attempt
// to create an XmlWebApplicationContext by default which will by default
// attempt to load WEB-INF/applicationContext.xml

/**
 * An extension to {@link XmlWebApplicationContext} that prevents the
 * default config location from being returned as
 * "/WEB-INF/applicationContext.xml".
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class NoDefaultXmlWebApplicationContext extends XmlWebApplicationContext {

    /**
     * @return null to indicate that there are no default Spring
     *         context configuration locations
     */
    @Override
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    protected String[] getDefaultConfigLocations() {
        return null;
    }
}
