package cn.ac.iscas.cloudeploy.v2.model.service.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.exception.NotFoundException;
import cn.ac.iscas.cloudeploy.v2.io.file.Md5CachingByteSource;
import cn.ac.iscas.cloudeploy.v2.model.dao.file.FileSourceDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.file.TempFileDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.file.FileSource;
import cn.ac.iscas.cloudeploy.v2.model.entity.file.TempFile;
import cn.ac.iscas.cloudeploy.v2.util.Constants;
import cn.ac.iscas.cloudeploy.v2.util.Uniques;

@Service
public class DefaultFileServiceImpl implements FileService {

	private String fileRoot;

	@Autowired
	private FileSourceDAO fileSourceDAO;

	@Autowired
	private TempFileDAO tmpFileDAO;

	@Autowired
	private ConfigService configService;

	@PostConstruct
	private void init() {
		fileRoot = configService
				.getConfigAsString(ConfigKeys.SERVICE_FILE_DEFAULT_ROOT);
	}

	@Override
	public String saveFile(ByteSource byteSource) throws IOException {
		Md5CachingByteSource md5ByteSource = Md5CachingByteSource
				.fromImmutableByteSource(byteSource);
		String md5 = md5ByteSource.getMd5();
		if (fileSourceDAO.findByMd5(md5) != null) {
			return md5ByteSource.getMd5();
		}
		String relativePath = calculateRelativePath(FileServiceUtils
				.randomString());
		File target = new File(fileRoot + File.separator + relativePath);
		FileUtils.forceMkdir(target.getParentFile());
		ByteSink byteSink = Files.asByteSink(target);
		md5ByteSource.copyTo(byteSink);
		FileSource file = new FileSource();
		file.setMd5(md5);
		file.setCredential(relativePath);
		file.setTyp("file");
		fileSourceDAO.save(file);
		return md5;
	}

	@Override
	public ByteSource findFile(String md5) throws FileNotFoundException {
		FileSource source = fileSourceDAO.findByMd5(md5);
		if (source == null) {
			throw new FileNotFoundException();
		}
		File file = new File(fileRoot + File.separator + source.getCredential());
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		return Files.asByteSource(file);
	}

	private String calculateRelativePath(String name) {
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
		Calendar calendar = Calendar.getInstance();
		String path = sdf.format(calendar.getTime()) + "/" + name;
		return path;
	}

	@Override
	public String generateDownloadURL(String md5) {
		return String
				.format(configService
						.getConfigAsString(ConfigKeys.SERVICE_FILE_URL_DOWNLOAD_FORMAT),
						configService.getConfigAsString(ConfigKeys.SERVER_ROOT),
						md5);
	}

	@Override
	public String readFileContent(String md5) throws IOException {
		ByteSource byteSource = findFile(md5);
		CharSource charSource = byteSource.asCharSource(Charset
				.forName(Constants.FILE_DEFAULT_CHARSET));
		return charSource.read();
	}

	@Override
	public String writeFileContent(String content) throws IOException {
		String md5 = FileServiceUtils.md5(content);
		if (fileSourceDAO.findByMd5(md5) != null) {
			return md5;
		}
		String relativePath = calculateRelativePath(FileServiceUtils
				.randomString());
		File target = new File(fileRoot + File.separator + relativePath);
		FileUtils.forceMkdir(target.getParentFile());
		ByteSink byteSink = Files.asByteSink(target);
		CharSink charSink = byteSink.asCharSink(Charset
				.forName(Constants.FILE_DEFAULT_CHARSET));
		charSink.write(content);
		FileSource file = new FileSource();
		file.setMd5(md5);
		file.setCredential(relativePath);
		file.setTyp("file");
		fileSourceDAO.save(file);
		return md5;
	}

	@Override
	public String generateTempKey() {
		TempFile f = new TempFile();
		f.setAccessKey(Uniques.getUniqueString());
		tmpFileDAO.save(f);
		return f.getAccessKey();
	}

	@Override
	public String saveTempFile(ByteSource byteSource, String accessKey)
			throws IOException {
		TempFile tmpFile = tmpFileDAO.findByAccessKey(accessKey);
		decideExpiration(tmpFile);
		String md5 = saveFile(byteSource);
		tmpFile.setLink(md5);
		tmpFileDAO.save(tmpFile);
		return md5;
	}

	@Override
	public String getTempFileKeyByAccessKey(String accessKey) {
		TempFile tmpFile = tmpFileDAO.findByAccessKey(accessKey);
		decideExpiration(tmpFile);
		if (tmpFile.getLink() == null) {
			throw new NotFoundException("file not found");
		}
		return tmpFile.getLink();
	}

	private void decideExpiration(TempFile tmpFile) {
		if (tmpFile == null
				|| tmpFile.getCreateTime().getTime() + tmpFile.getTimeToLive() > System
						.nanoTime()) {// 临时文件已过期
			throw new NotFoundException("file already expired");
		}
	}

	@Override
	public String savePuppetFile(ByteSource byteSource) throws IOException{
		Md5CachingByteSource md5ByteSource = Md5CachingByteSource
				.fromImmutableByteSource(byteSource);
		String md5 = md5ByteSource.getMd5();
		if (fileSourceDAO.findByMd5(md5) != null) {
			return md5ByteSource.getMd5();
		}
		String relativePath = calculateRelativePath(FileServiceUtils
				.randomString());
		InputStream zin = byteSource.openStream();
		String absolutePath = fileRoot + File.separator + relativePath;
		FileServiceUtils.Unzip(zin, absolutePath);
		
		FileSource file = new FileSource();
		file.setMd5(md5);
		file.setCredential(relativePath);
		file.setTyp("puppet");
		fileSourceDAO.save(file);
		return md5;
	}
	@Override
	public ZipBuilder buildZipBuilder(){
		String relativePath = calculateRelativePath(FileServiceUtils
				.randomString()) + ".zip";
		String absolutePath = fileRoot + File.separator + relativePath;
		return new ZipBuilder(absolutePath, relativePath);
	}
	@Override
	public String saveZip(ZipBuilder builder) throws IOException{
		FileServiceUtils.zip(builder.getTargetFilePath(), builder.getFiles());
		ByteSource zipFile = Files.asByteSource(new File(builder.getTargetFilePath()));
		Md5CachingByteSource md5ByteSource = Md5CachingByteSource
				.fromImmutableByteSource(zipFile);
		String md5 = md5ByteSource.getMd5();
		if (fileSourceDAO.findByMd5(md5) != null) {
			FileServiceUtils.delete(builder.getTargetFilePath());
			return md5ByteSource.getMd5();
		}
		FileSource file = new FileSource();
		file.setMd5(md5);
		file.setCredential(builder.getRelativePath());
		file.setTyp("file");
		fileSourceDAO.saveAndFlush(file);
		return md5;
	}
}
