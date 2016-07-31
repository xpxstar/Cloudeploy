package cn.ac.iscas.cloudeploy.v2.controller.file;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController.DResponseBuilder;
import cn.ac.iscas.cloudeploy.v2.io.file.MultipartFileByteSource;
import cn.ac.iscas.cloudeploy.v2.io.file.ResponseByteSink;
import cn.ac.iscas.cloudeploy.v2.model.dao.file.TempFileDAO;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.Anonymous;
import cn.ac.iscas.cloudeploy.v2.util.RandomUtils;

@Controller
@Transactional
@RequestMapping(value = "v2/files")
public class FileController {
	@Autowired
	private FileService fileService;

	@Autowired
	private TempFileDAO tmpFileDAO;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object uploadFile(@RequestParam("file") MultipartFile file)
			throws IOException {
		return DResponseBuilder
				.instance()
				.add("fileKey",
						fileService.saveFile(new MultipartFileByteSource(file)))
				.build();
	}
	
	/**
	 * @description upload puppet modules zipfile
	 * @param file
	 * @return file Md5-key
	 * @throws IOException Object
	 * @author xpxstar@gmail.com
	 * 2015年11月13日 下午3:38:45
	 */
	@RequestMapping(value = { "/puppet" }, method = RequestMethod.POST)
	@ResponseBody
	public Object uploadPuppetFile(@RequestParam("file") MultipartFile file)
			throws IOException {
		return DResponseBuilder
				.instance()
				.add("fileKey",
						fileService.savePuppetFile(new MultipartFileByteSource(file)))
				.build();
	}

	@RequestMapping(value = { "/{fileKey}" }, method = RequestMethod.POST)
	@ResponseBody
	public Object requestFileDownloadURL(@PathVariable("fileKey") String fileKey) {
		return DResponseBuilder.instance()
				.add("url", fileService.generateDownloadURL(fileKey)).build();
	}

	@RequestMapping(value = { "/{downloadKey}" }, method = RequestMethod.GET)
	@Anonymous
	public void downloadFile(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("downloadKey") String downloadKey,
			@RequestParam(value = "name", required = false, defaultValue = "") String name)
			throws IOException {
		ByteSource byteSource = fileService.findFile(downloadKey);
		launchDownloadResponse(response, byteSource,
				StringUtils.isEmpty(name) ? RandomUtils.randomString() : name);
	}

	@RequestMapping(value = { "/content/{fileKey}" }, method = RequestMethod.GET)
	@ResponseBody
	public Object requestFileContent(@PathVariable("fileKey") String fileKey)
			throws IOException {
		return DResponseBuilder.instance()
				.add("content", fileService.readFileContent(fileKey)).build();
	}

	@RequestMapping(value = { "/content" }, method = RequestMethod.POST)
	@ResponseBody
	public Object writeContentToFile(
			@RequestParam("fileContent") String fileContent) throws IOException {
		return DResponseBuilder.instance()
				.add("fileKey", fileService.writeFileContent(fileContent))
				.build();
	}

	/**
	 * 创建一个临时文件对象，返回accessKey
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/tmp/accesskey" }, method = RequestMethod.POST)
	@ResponseBody
	public Object generateAccessKey() {
		return DResponseBuilder.instance()
				.add("accessKey", fileService.generateTempKey()).build();
	}

	@RequestMapping(value = { "/tmp/{accessKey}" }, method = RequestMethod.GET)
	@ResponseBody
	public Object getTempFileKey(@PathVariable("accessKey") String accessKey) {
		return DResponseBuilder
				.instance()
				.add("fileKey",
						fileService.getTempFileKeyByAccessKey(accessKey))
				.build();
	}

	/**
	 * 上传一个临时文件
	 * 
	 * @param file
	 * @param accessKey
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "/tmp/upload" }, method = RequestMethod.POST)
	@ResponseBody
	@Anonymous
	public Object uploadTempFile(@RequestParam("file") MultipartFile file,
			@RequestParam("accessKey") String accessKey) throws IOException {
		return DResponseBuilder
				.instance()
				.add("fileKey",
						fileService.saveTempFile(new MultipartFileByteSource(
								file), accessKey)).build();
	}

	private void launchDownloadResponse(HttpServletResponse response,
			ByteSource byteSource, String fileName) throws IOException {
		response.reset();
		response.setContentType("application/octet-stream");
		response.addHeader(HttpHeaders.CONTENT_LENGTH,
				String.valueOf(byteSource.size()));
		String name = new String(fileName.getBytes("GB2312"), "ISO8859-1");
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + name + "\"");
		byteSource.copyTo(new ResponseByteSink(response));
	}
}
