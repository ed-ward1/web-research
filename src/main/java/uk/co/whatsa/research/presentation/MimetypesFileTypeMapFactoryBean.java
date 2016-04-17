package uk.co.whatsa.research.presentation;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * A {@link FactoryBean} used to construct an instance of
 * {@link MimetypesFileTypeMap}. The instance is used to identify the
 * resource mime type and the Java class used to persist the resource.
 */
public class MimetypesFileTypeMapFactoryBean implements FactoryBean<MimetypesFileTypeMap> {
    /**
     * The location of the file resource containing details about
     * extensions and their file types.
     */
    private String mimeTypesFileResourcePath;

    @Override
    public final MimetypesFileTypeMap getObject() throws Exception {
        Resource resource = new ClassPathResource(mimeTypesFileResourcePath);
        return new MimetypesFileTypeMap(resource.getInputStream());
    }

    @Override
    public final Class<?> getObjectType() {
        return MimetypesFileTypeMap.class;
    }

    @Override
    public final boolean isSingleton() {
        return true;
    }

    /**
     * Property setter method.
     * @param mimeTypesFileResourcePath {@link #mimeTypesFileResourcePath}
     */
    public final void setMimeTypesFileResourcePath(final String mimeTypesFileResourcePath) {
        this.mimeTypesFileResourcePath = mimeTypesFileResourcePath;
    }

}
