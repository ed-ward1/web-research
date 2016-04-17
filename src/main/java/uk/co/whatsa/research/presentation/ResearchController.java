package uk.co.whatsa.research.presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.co.whatsa.research.ResearchRuntimeException;
import uk.co.whatsa.research.dao.ResearchDAO;

/**
 * An ad-hoc collection of {@link RequestMapping} handler methods.
 * TODO group the handler methods more appropriately on different controller classes
 */
@Controller
public class ResearchController {
    // private static final Logger LOG =
    // LoggerFactory.getLogger(ResearchController.class);

    /** A data access object for persistent CRUD operations. */
    @Autowired
    private ResearchDAO researchDAO;

    /**
     * The main "welcome" index page.
     */
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public final void index() {
        return;
    }

    /**
     * Tails a file from a given offset. If the file contains data
     * from the "offset" passed in the parameter the data is read and
     * returned. If no data is available from the given offset the
     * method will wait for a number of seconds until data is
     * available. The content is checked every 500ms. If no data is
     * forthcoming then the method times out, returns the offset as
     * the current file length and no data is returned.
     * 
     * @param tailedFile contains the name of the file an offset from
     *            which data should be read
     * @return a {@link TailedFile} instance holding the data and next
     *         offset (the current end of file position), which is
     *         received by the browser in the form of a JSON string
     */
    @RequestMapping(path = "/tail", method = RequestMethod.POST)
    public final @ResponseBody TailedFile tailPost(final @ModelAttribute TailedFile tailedFile) {
        final File file = new File(tailedFile.getFileName());
        final int eos = -1;
        FileReader fr = null;
        final int maxBufferSize = 5000;
        try {
            fr = new FileReader(file);
            fr.skip(tailedFile.getOffset());
            final char[] buffer = new char[maxBufferSize];
            int charsRead = fr.read(buffer);

            if (charsRead == eos) {
                // Time to wait for input before giving up
                final long waitTime = 20000;
                // Number of millis to sleep before checking again
                final long sleepDelay = 500;
                final long startTime = System.currentTimeMillis();
                boolean notTimedOut = true;
                do {
                    try {
                        Thread.sleep(sleepDelay);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                    charsRead = fr.read(buffer);
                    notTimedOut = System.currentTimeMillis() - startTime < waitTime;
                } while (charsRead == eos && notTimedOut && tailedFile.getOffset() == file.length());
                if (!notTimedOut) {
                    tailedFile.setErrorMessage("timed out");
                }
            }
            if (charsRead == eos) {
                charsRead = 0;
                tailedFile.setOffset(file.length());
            } else {
                tailedFile.setOffset(tailedFile.getOffset() + charsRead);
            }
            tailedFile.setData(new String(buffer, 0, charsRead));
        } catch (FileNotFoundException e) {
            tailedFile.setErrorMessage("file not found: " + file.getPath());
        } catch (IOException e) {
            tailedFile.setErrorMessage("failed to read file: " + file.getPath());
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    throw new ResearchRuntimeException(e);
                }
            }
        }

        return tailedFile;
    }

    /**
     * TODO this method should return a collection of WebPage
     * instances rather than a service interface.
     * 
     * @return a {@link ModelMap} instance with a {@link ResearchDAO}
     *         instance keyed by the string "research"
     */
    @RequestMapping(path = "/listWebPages", method = RequestMethod.GET)
    public final ModelMap listWebPages() {
        return new ModelMap("research", researchDAO);
    }
}
