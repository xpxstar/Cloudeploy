package cn.ac.iscas.cloudeploy.v2.util;

import java.util.Random;

public class RandomUtils {
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
}
