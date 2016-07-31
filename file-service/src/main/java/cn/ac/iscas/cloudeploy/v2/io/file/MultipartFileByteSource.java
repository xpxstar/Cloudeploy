package cn.ac.iscas.cloudeploy.v2.io.file;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.ByteSource;

public class MultipartFileByteSource extends ByteSource {
	private MultipartFile file;

	public MultipartFileByteSource(MultipartFile file) {
		this.file = file;
	}

	public String getName() {
		return file.getOriginalFilename();
	}

	@Override
	public InputStream openStream() throws IOException {
		return file.getInputStream();
	}

	@Override
	public long size() throws IOException {
		return file.getSize();
	}

}
