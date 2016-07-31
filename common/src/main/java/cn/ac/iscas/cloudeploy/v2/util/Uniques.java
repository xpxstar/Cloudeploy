package cn.ac.iscas.cloudeploy.v2.util;

import java.util.UUID;

public class Uniques {
	public static String getUniqueString() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}
	
	public static void main(String[] args) {
		System.out.println(getUniqueString());
	}
}
