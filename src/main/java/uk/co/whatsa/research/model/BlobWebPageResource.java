package uk.co.whatsa.research.model;

/**
 * Represents a web page resource consisting of byte data.
 */
public class BlobWebPageResource extends WebPageResource {
    /** The byte array used to store the BLOB data. */
    private byte[] blob;

    /**
     * Default constructor.
     */
    public BlobWebPageResource() {
        super();
    }

    /**
     * @param mimeContentType {@link WebPageResource#getMimeContentType()}
     */
    public BlobWebPageResource(final String mimeContentType) {
        super(mimeContentType);
    }

    /**
     * @return {@link #blob}
     */
    public final byte[] getResourceBlob() {
        return blob;
    }

    /**
     * @param blob {@link #blob}
     */
    public final void setResourceBlob(final byte[] blob) {
        this.blob = blob.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getResourceData() {
        return getResourceBlob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setResourceData(final byte[] resourceData) {
        setResourceBlob(resourceData);
    }
}
