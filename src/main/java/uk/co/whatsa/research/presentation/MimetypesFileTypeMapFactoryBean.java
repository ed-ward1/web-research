package uk.co.whatsa.research.presentation;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MimetypesFileTypeMapFactoryBean implements FactoryBean<MimetypesFileTypeMap> {
	private String mimeTypesFileResourcePath;
	
	@Override
	public MimetypesFileTypeMap getObject() throws Exception {
		Resource resource = new ClassPathResource(mimeTypesFileResourcePath);
		return new MimetypesFileTypeMap(resource.getInputStream());
	}

	@Override
	public Class<?> getObjectType() {
		return MimetypesFileTypeMap.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setMimeTypesFileResourcePath(String mimeTypesFileResourcePath) {
		this.mimeTypesFileResourcePath = mimeTypesFileResourcePath;
	}

}
