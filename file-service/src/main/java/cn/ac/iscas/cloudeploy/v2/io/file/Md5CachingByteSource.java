package cn.ac.iscas.cloudeploy.v2.io.file;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public class Md5CachingByteSource extends ByteSource {
	private ByteSource source;

	private String md5 = null;

	private Md5CachingByteSource(ByteSource source) {
		this.source = source;
	}

	public static Md5CachingByteSource fromImmutableByteSource(ByteSource source) {
		if (source instanceof Md5CachingByteSource) {
			return (Md5CachingByteSource) source;
		}
		return new Md5CachingByteSource(source);
	}

	public String getMd5() throws IOException {
		if (md5 == null) {
			md5 = hash(Hashing.md5()).toString();
		}
		return md5;
	}

	@Override
	public InputStream openStream() throws IOException {
		return source.openStream();
	}

	@Override
	public long size() throws IOException {
		return source.size();
	}

	@Override
	public ByteSource slice(long offset, long length) {
		return source.slice(offset, length);
	}
}
