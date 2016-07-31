package cn.ac.iscas.cloudeploy.v2.model.service.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.ByteSource;
public interface FileService {
	public String saveFile(ByteSource byteSource) throws IOException;

	public ByteSource findFile(String md5) throws FileNotFoundException;

	public String generateDownloadURL(String md5);

	public String readFileContent(String md5) throws IOException;

	public String writeFileContent(String content) throws IOException;

	// interfaces for temp file
	public String generateTempKey();

	public String saveTempFile(ByteSource byteSource, String accessKey)
			throws IOException;
	
	public String getTempFileKeyByAccessKey(String accessKey);

	public String savePuppetFile(ByteSource byteSource) throws IOException;

	ZipBuilder buildZipBuilder();

	String saveZip(ZipBuilder builder) throws IOException;
	
	public static class ZipBuilder{
		private Map<String, ByteSource> files;
		private String targetFilePath;
		private String relativePath;
		
		public ZipBuilder(String targetFilePath, String relativePath){
			this.targetFilePath = targetFilePath;
			this.relativePath = relativePath;
			this.files = new HashMap<>();
		}
		
		public void addFileToPackage(String filename, ByteSource byteSource){
			files.put(filename,byteSource);
		}

		public String getRelativePath() {
			return relativePath;
		}

		public String getTargetFilePath() {
			return targetFilePath;
		}

		public Map<String, ByteSource> getFiles() {
			return files;
		}
	}
}
