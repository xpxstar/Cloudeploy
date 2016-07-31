package cn.ac.iscas.cloudeploy.v2.model.service.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.io.ByteSource;

public class FileServiceUtils {
	private static int LENGTH = 8;
	private static String SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static Random RANDOM = new Random();

	public static String randomString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < LENGTH; i++) {
			int num = RANDOM.nextInt(SEED.length());
			builder.append(SEED.charAt(num));
		}
		return builder.toString();
	}
	
	public static String md5(String target) {
		return DigestUtils.md5Hex(target);
	}

	private static void Goon(ZipInputStream Zin, String targetDic) {
		BufferedInputStream Bin = new BufferedInputStream(Zin);
		File Fout = null;
		ZipEntry entry;
		try {
			while ((entry = Zin.getNextEntry()) != null) {
				if (entry.isDirectory()) {
				} else {
					Fout = new File(targetDic, entry.getName());
					if (!Fout.exists()) {
						(new File(Fout.getParent())).mkdirs();
					}
					FileOutputStream out = new FileOutputStream(Fout);
					BufferedOutputStream Bout = new BufferedOutputStream(out);
					int b;
					while ((b = Bin.read()) != -1) {
						Bout.write(b);
					}
					Bout.close();
					out.close();
				}
			}
			Bin.close();
			Zin.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void Unzip(InputStream sourceFile, String targetDic) {
		ZipInputStream Zin = new ZipInputStream(sourceFile);// 输入源zip路径
		Goon(Zin, targetDic);
	}

	public static void Unzip(String sourceFile, String targetDic) {
		try {
			ZipInputStream Zin;
			Zin = new ZipInputStream(new FileInputStream(sourceFile));
			Goon(Zin, targetDic);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 输入源zip路径

	}

	public static void zip(String targetFileName, Map<String, ByteSource> bytesources) {
		Path path = Paths.get(targetFileName);
		if(!Files.exists(path)){
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (FileOutputStream fos = new FileOutputStream(targetFileName);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ZipOutputStream zos = new ZipOutputStream(bos)) {
			for(Entry<String, ByteSource> entry : bytesources.entrySet()){
				ZipEntry fileEntry = new ZipEntry(entry.getKey());
				zos.putNextEntry(fileEntry);
				entry.getValue().copyTo(zos);
				zos.closeEntry();
			}
			zos.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delete(String targetFilePath) throws IOException {
		Path path = Paths.get(targetFilePath);
		Files.deleteIfExists(path);
	}
}
