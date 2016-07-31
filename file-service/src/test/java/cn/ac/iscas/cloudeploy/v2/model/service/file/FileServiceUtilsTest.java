package cn.ac.iscas.cloudeploy.v2.model.service.file;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import com.google.common.io.ByteSource;

import cn.ac.iscas.cloudeploy.v2.io.file.Md5CachingByteSource;

public class FileServiceUtilsTest {

	@Test
	public void testZip(){
		Path path = Paths.get("src/test/resources");
		Path targetPath = path.resolve("packetTest.zip");
		Map<String, ByteSource> bytesources = new HashMap<>();
		bytesources.put("scripts/install.sh", ByteSource.wrap("install.sh".getBytes()));
		bytesources.put("specific.yaml", ByteSource.wrap("specific.yaml".getBytes()));
		FileServiceUtils.zip(targetPath.toString(), bytesources);
		try {
			Files.delete(targetPath);
		} catch (IOException e) {
			e.printStackTrace();
			fail("failed delete packetTest.zip");
		}
	}
	@Test
	public void test_zip_file_and_zip_byte_source_is_not_equal(){
		Path path = Paths.get("src/test/resources");
		Path targetPath = path.resolve("packetTest.zip");
		Map<String, ByteSource> bytesources = new HashMap<>();
		bytesources.put("scripts/install.sh", ByteSource.wrap("install.sh".getBytes()));
		bytesources.put("specific.yaml", ByteSource.wrap("specific.yaml".getBytes()));
		FileServiceUtils.zip(targetPath.toString(), bytesources);
		
		try {
			ByteSource zipFile = com.google.common.io.Files.asByteSource(targetPath.toFile());
			Md5CachingByteSource md5ByteSource = Md5CachingByteSource
					.fromImmutableByteSource(zipFile);
			String md5 = md5ByteSource.getMd5();
			
			ByteSource testSource = ByteSource.concat(bytesources.values());
			Md5CachingByteSource testMd5ByteSource = Md5CachingByteSource
					.fromImmutableByteSource(testSource);
			String testMd5 = testMd5ByteSource.getMd5();
			
			assertNotEquals(md5, testMd5);
			
			Files.delete(targetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
