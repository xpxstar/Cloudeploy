package cn.ac.iscas.cloudeploy.v2.util;

import org.apache.commons.lang3.StringUtils;

public class DeployUtils {
	public static String getLastSegmentOfName(String str) {
		return getLastSegmentsOfName(str, 1, Constants.NAME_SEPARATOR,
				Constants.NAME_SEPARATOR);
	}

	public static String getLastSegmentsOfName(String str, int segNum) {
		return getLastSegmentsOfName(str, segNum, Constants.NAME_SEPARATOR,
				Constants.NAME_SEPARATOR);
	}

	public static String getLastSegmentsOfName(String str, int segNum,
			String sourceSeparator, String targetSeparator) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		String[] segments = str.split(sourceSeparator);

		if (segments.length <= segNum) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		for (int start = segments.length - segNum, i = start; i < segments.length; i++) {
			if (i == start) {
				sb.append(segments[i]);
			} else {
				sb.append(targetSeparator + segments[i]);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(getLastSegmentsOfName(
				"cloudeploy::default::tomcat::config", 2));
	}
}
