package cn.ac.iscas.cloudeploy.v2.io.file;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteSink;

public class ResponseByteSink extends ByteSink {

	private HttpServletResponse response;

	@SuppressWarnings("unused")
	private ResponseByteSink() {

	}

	public ResponseByteSink(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public OutputStream openStream() throws IOException {
		return response.getOutputStream();
	}

}
