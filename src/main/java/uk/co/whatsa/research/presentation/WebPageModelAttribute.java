package uk.co.whatsa.research.presentation;

import java.net.MalformedURLException;
import java.net.URL;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A value holder class encapsulating the details of a WebPage to be
 * captured.
 */
public class WebPageModelAttribute {
    /** Used to log debug and error messages. */
    private static final Logger LOG = LoggerFactory.getLogger(WebPageModelAttribute.class);

    /** The URL of the {@link WebPage} to cap. */
    private String url = "";

    /** A comment to associate with the {@link WebPage}. */
    private String comment = "";

    /** The date the {@link WebPage} was downloaded and stored. */
    private DateTime recordedDate = DateTime.now();

    /**
     * @return {@link #url}
     */
    public final String getUrl() {
        return url;
    }

    /**
     * @param url {@link #url}
     */
    public final void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return {@link #comment}
     */
    public final String getComment() {
        return comment;
    }

    /**
     * @param comment {@link #comment}
     */
    public final void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * @return {@link #recordedDate}
     */
    public final DateTime getRecordedDate() {
        return recordedDate;
    }

    /**
     * @param recordedDate {@link #recordedDate}
     */
    public final void setRecordedDate(final DateTime recordedDate) {
        this.recordedDate = recordedDate;
    }

    /**
     * @return The domain name from the URL used to read the web page.
     */
    public final String getDomainName() {
        String domainName = "";
        try {
            domainName = new URL(getUrl()).getHost();
        } catch (MalformedURLException e) {
            LOG.error("Malformed URL for webpage : ", getUrl());
        }
        return domainName;
    }
}
