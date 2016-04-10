package uk.co.whatsa.spring.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

/**
 * Used by the UI conversion service to convert UI string dates to and from joda
 * {@link DateTime} instances. The format of the {@code DateTime} instance is determined by a
 * resource bundle {@code MessageSource} property {@code 'datetime.format'}.
 */
public class CustomDateTimeFormatter implements Formatter<DateTime> {

    @Autowired
    private MessageSource messageSource;


    public CustomDateTimeFormatter() {
        super();
    }

    public DateTime parse(final String text, final Locale locale) throws ParseException {
        final DateTimeFormatter formatter = createDateTimeFormat(locale);
        return formatter.parseDateTime(text);
    }

    public String print(final DateTime dateTime, final Locale locale) {
        final DateTimeFormatter formatter = createDateTimeFormat(locale);
        return dateTime.toString(formatter);
    }

    private DateTimeFormatter createDateTimeFormat(final Locale locale) {
        final String formatPattern = this.messageSource.getMessage("datetime.format", null, locale);
        return DateTimeFormat.forPattern(formatPattern);
    }

}
