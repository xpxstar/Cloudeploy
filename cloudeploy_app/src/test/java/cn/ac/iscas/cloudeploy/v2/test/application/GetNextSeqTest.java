package cn.ac.iscas.cloudeploy.v2.test.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetNextSeqTest {

	public int getNextSeq() {
		List<Integer> origin = new ArrayList<>();
		origin.addAll(Arrays.asList(1,2,3,4));
		Collections.sort(origin, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});

		for (int i = 0; i < 5 && i < origin.size(); i++) {
			int seq = origin.get(i);
			if (i + 1 != seq) {
				return i + 1;
			}
		}
		return origin.size() + 1;
	}

	public static void main(String[] args) {
		GetNextSeqTest test = new GetNextSeqTest();
		System.out.println(test.getNextSeq());
	}
}
