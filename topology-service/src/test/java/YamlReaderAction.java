import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;


public class YamlReaderAction {

	@Test
	@Ignore
	public void test() throws IOException {
		URL resource = getClass().getResource("test.yaml");
		assertNotNull(resource);
		
		InputStream ins = resource.openStream();
		
		TypeDescription typeDescription = new TypeDescription(Configuration.class);
		TypeDescription databaseDescription = new TypeDescription(Database.class);
		databaseDescription.putMapPropertyType("kasses", String.class, Kasse.class);
		
		Constructor constructor = new Constructor(Configuration.class);
		constructor.addTypeDescription(typeDescription);
		constructor.addTypeDescription(databaseDescription);
		Yaml yaml = new Yaml(constructor);
		yaml.setBeanAccess(BeanAccess.FIELD);
		Configuration configuration = (Configuration) yaml.load(ins);
		
		Database database = configuration.getDatabase();
		assertEquals("root", database.getUsername());
		assertEquals("test", database.getPassword());
		
		Kasse kasse = configuration.getKasse();
		assertEquals("fabienneKasse", kasse.getName());
		assertEquals(20, kasse.getProzent());
	}
	
	@Test
	public void testNodeType() throws IOException{
		URL resource = getClass().getResource("nodeType.yaml");
		assertNotNull(resource);
		InputStream ins = resource.openStream();
		Constructor constructor = new Constructor(NodeType.class);
		TypeDescription typeDescription = new TypeDescription(NodeType.class);
		constructor.addTypeDescription(typeDescription);
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(constructor, new NullRepresenter(),options);
		yaml.setBeanAccess(BeanAccess.FIELD);
		NodeType nodetype = (NodeType) yaml.load(ins);
		assertEquals("cloudeploy.nodes.Compute", nodetype.getName());
		System.out.println(yaml.dump(nodetype));
	}
	
	@Test
	public void testLoadAllMethod(){
		
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
class Configuration {

	private Database database;
	
	private Kasse kasse;

	public void setDatabase(Database database) {
		this.database = database;
	}

	public Database getDatabase() {
		return database;
	}

	public void setKasse(Kasse kasse) {
		this.kasse = kasse;
	}

	public Kasse getKasse() {
		return kasse;
	}
	
}


class Database {

	private String username;
	
	private String password;
	
	private List<Kasse> kasses;

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
	
}

class Kasse {

	private String name;
	
	private long prozent;

	public void setProzent(long prozent) {
		this.prozent = prozent;
	}

	public long getProzent() {
		return prozent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}