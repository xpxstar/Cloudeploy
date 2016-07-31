package cn.ac.iscas.cloudeploy.v2.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ForEachHelper {
	public static <T> List<T> of(List<T> list){
		if(list == null) return Collections.emptyList();
		else return list;
	}
	public static <T> Set<T> of(Set<T> set){
		if(set == null) return Collections.emptySet();
		else return set;
	}
	public static <T> Collection<T> of(Collection<T> collections) {
		if(collections == null) return Collections.emptyList();
		return collections;
	}
}
