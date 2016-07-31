package cn.ac.iscas.cloudeploy.v2.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;


public class YamlUtils {
	public static <T> String dumper(T object){
		Constructor constructor = new Constructor(object.getClass());
		TypeDescription typeDescription = new TypeDescription(object.getClass());
		constructor.addTypeDescription(typeDescription);
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(constructor, new NullRepresenter(),options);
		yaml.setBeanAccess(BeanAccess.FIELD);
		return yaml.dump(object);
	}
	
	public static <T> T loadAs(String content, Class<T> clazz){
		Yaml yaml = new Yaml();
		return yaml.loadAs(content, clazz);
	}
	
	public static <T> T loadAs(Path path, Class<T> clazz){
		Yaml yaml = new Yaml();
		try {
			return yaml.loadAs(Files.newBufferedReader(path, Charsets.UTF_8), clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T loadAs(ByteSource source, Class<T> clazz){
		Yaml yaml = new Yaml();
		try (InputStream input = source.openStream()){
			return yaml.loadAs(input, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static class NullRepresenter extends Representer{
		@Override
		protected NodeTuple representJavaBeanProperty(Object javaBean,
				Property property, Object propertyValue, Tag customTag) {
			if(propertyValue == null || propertyValue == Collections.emptyList()){
				return null;
			}else{
				return super.representJavaBeanProperty(javaBean, property, propertyValue,
						customTag);
			}
		}
	}
}


