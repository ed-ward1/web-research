package uk.co.whatsa.spring;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;

public class UrlPrefixViewResolver implements ViewResolver, Ordered {
	private String prefix;
	private int order = 1;
	
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}
	
	public void setOrder(final int order) {
		this.order = order;
	}
	
	@Override
	public View resolveViewName(final String viewName, final Locale locale) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		final String contextPath = request.getContextPath();
		final String uri = request.getRequestURI().substring(contextPath.length());
		if (uri.startsWith(prefix)) {
			return new InternalResourceView();
		}
		return null;
	}

	@Override
	public int getOrder() {
		return order;
	}
}
