package cn.ac.iscas.cloudeploy.v2.model.entity.topology;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;

public class ServiceTemplateTest {

	@Test
	public void test() throws IOException {
		URL resource = getClass().getResource("/modelExample.yaml");
		assertNotNull(resource);
		InputStream ins = resource.openStream();
		Constructor constructor = new Constructor(ServiceTemplate.class);
		TypeDescription typeDescription = new TypeDescription(ServiceTemplate.class);
		constructor.addTypeDescription(typeDescription);
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(constructor, new NullRepresenter(),options);
		yaml.setBeanAccess(BeanAccess.FIELD);
		ServiceTemplate template = (ServiceTemplate) yaml.load(ins);
		assertEquals("freecsm_example", template.getName());
		System.out.println(yaml.dump(template));
	}
}

class NullRepresenter extends Representer{
	@Override
	protected NodeTuple representJavaBeanProperty(Object javaBean,
			Property property, Object propertyValue, Tag customTag) {
		if(propertyValue == null){
			return null;
		}else{
			return super.representJavaBeanProperty(javaBean, property, propertyValue,
					customTag);
		}
	}
}