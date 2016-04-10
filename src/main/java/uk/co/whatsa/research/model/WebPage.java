package uk.co.whatsa.research.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import uk.co.whatsa.persistence.Persistent;

public class WebPage extends Persistent {

	////////////////////////////////////
	// The persistent state of a WebPage

	/** The internet URL of the page that was saved. */
	private String url;

	/** The date the page was published. */
	private DateTime pageDate;

	/** The date the page was recorded / saved. */
	private DateTime recordedDate;

	/** Any comments associated with the page. */
	private String comment;

	/** The root HTML resource that was "saved" and which is displayed. */
	private WebPageResource mainResource;

	/**
	 * A collection of all the "saved" resources, including the "main" one associated with /
	 * comprising this page.
	 */
	private Set<WebPageResource> resources = new HashSet<WebPageResource>();

	// The persistent state of a WebPage
	////////////////////////////////////

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DateTime getPageDate() {
		return pageDate;
	}

	public void setPageDate(DateTime pageDate) {
		this.pageDate = pageDate;
	}

	public DateTime getRecordedDate() {
		return recordedDate;
	}

	public void setRecordedDate(DateTime recordedDate) {
		this.recordedDate = recordedDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public WebPageResource getMainResource() {
		return mainResource;
	}

	public void setMainResource(WebPageResource mainResource) {
		this.mainResource = mainResource;
	}

	public Set<WebPageResource> getResources() {
		return resources;
	}

	public void setResources(Set<WebPageResource> resources) {
		this.resources = resources;
	}

	///////////////////////////////////////////////////////////////////////
	// Behaviour and functionality in addition to the persistent properties.

	public void addResource(WebPageResource resource) {
		getResources().add(resource);
		resource.setWebPage(this);
	}

	/**
	 * @return The domain name from the URL used to read the web page.
	 */
	public String getDomainName() {
		String domainName = "";
		try {
			domainName = new URL(getUrl()).getHost();
		} catch (MalformedURLException e) {
			LOG.error("Malformed URL for webpage : ", getUrl());
		}
		return domainName;
	}
}
