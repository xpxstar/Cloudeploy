package cn.ac.iscas.cloudeploy.v2.util.application;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;

public class YamlUtilsTest {

	@Test
	public void testLoadAsPathClassOfT() {
		YamlUtils.loadAs(Paths.get("C:\\Users\\RichardLcc\\Desktop\\jDxZaX5O\\specificFile.yaml"), SpecificEntity.class);
	}

}
