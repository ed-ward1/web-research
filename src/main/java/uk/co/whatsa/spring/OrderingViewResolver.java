package uk.co.whatsa.spring;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.core.Ordered;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;

/**
 * A {@code ViewResolver} implementation to work around the broken "order" property on other
 * {@code ViewResolver} implementations. Other {@code ViewResolver} implementations will claim to
 * resolve the view name only to throw an Exception later when attempting to read the associated
 * resource. The {@code order} property does not help in these instances since the
 * {@code ViewResolver}s have already been queried. Usually the first resolver given the chance to
 * resolve the view will always claim to resolve a view no matter the name.
 * <p />
 * This implementation however will verify the resource can actually be read by the resolver before
 * associating the view with the resolver. If the resource associated with the view name cannot be
 * read then this resolver will give the next view resolver in the list the opportunity to resolve
 * the view. The results are cached in a {@code Map}.
 */
public class OrderingViewResolver implements ViewResolver, Ordered, ServletContextAware {
	private static Object[] emptyObjectArray = new Object[0];
	private static Class<?>[] emptyClassArray = new Class[0];

	private int order = Ordered.HIGHEST_PRECEDENCE;
	private List<ViewResolver> viewResolvers;
	private Map<String, ViewResolver> viewNameMap = new HashMap<String, ViewResolver>();
	private ServletContext servletContext;

	/**
	 * @param viewResolvers the list of resolvers used to resolve view names. The resolvers will be
	 *            queried in the order they appear in the list.
	 */
	public void setViewResolvers(List<ViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	@Override
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		View view = null;
		Exception ex = null;
		ViewResolver vr = viewNameMap.get(viewName);
		if (vr != null) {
			view = vr.resolveViewName(viewName, locale);
		} else {
			for (ViewResolver viewResolver : viewResolvers) {
				try {
					if (viewExists(viewName, locale, viewResolver)) {
						view = viewResolver.resolveViewName(viewName, locale);
						viewNameMap.put(viewName, viewResolver);
						break;
					}
				} catch (Exception e) {
					ex = e;
				}
			}
		}

		if (view == null && ex != null) {
			throw ex;
		}

		return view;
	}

	private boolean viewExists(String viewName, Locale locale, ViewResolver viewResolver) {
		try {
			if (viewResolver instanceof ThymeleafViewResolver) {
				return viewExistsThymeleafViewResolver(viewName, locale, (ThymeleafViewResolver) viewResolver);
			}
			if ((viewResolver instanceof UrlBasedViewResolver)) {
				return viewExistsUrlBasedViewResolver(viewName, (UrlBasedViewResolver) viewResolver);
			}
		} catch (Exception e) {
		}

		return false;
	}

	private boolean viewExistsThymeleafViewResolver(String viewName, Locale locale, ThymeleafViewResolver viewResolver)
			throws Exception {
		ITemplateEngine templateEngine = viewResolver.getTemplateEngine();
		IEngineConfiguration configuration = templateEngine.getConfiguration();
		Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
		for (ITemplateResolver templateResolver : templateResolvers) {
			TemplateResolution resolution = templateResolver.resolveTemplate(configuration, null, viewName, null);
			ITemplateResource resource = resolution.getTemplateResource();
			if (resource.exists()) {
				return true;
			}
		}
		return false;
	}

	private boolean viewExistsUrlBasedViewResolver(String viewName, UrlBasedViewResolver viewResolver)
			throws Exception {
		Method method = UrlBasedViewResolver.class.getDeclaredMethod("getPrefix", emptyClassArray);
		method.setAccessible(true);
		String prefix = method.invoke(viewResolver, emptyObjectArray).toString();

		method = UrlBasedViewResolver.class.getDeclaredMethod("getSuffix", emptyClassArray);
		method.setAccessible(true);
		String suffix = method.invoke(viewResolver, emptyObjectArray).toString();

		String path = new StringBuilder(prefix).append(viewName).append(suffix).toString();

		ServletContextResource resource = new ServletContextResource(servletContext, path);
		return resource.exists();
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}