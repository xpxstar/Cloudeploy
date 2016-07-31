package cn.ac.iscas.cloudeploy.v2.test.configuration;

import java.util.List;

import org.junit.Test;

import lombok.Builder;
import lombok.Singular;

public class TestSingular {
	@Test
	public void test(){
		B b= B.builder().build();
		D d = D.builder().build();
		add(b.blists, "111");
		int a = d.dlists.get(0);
	}
	
	private <T> void add(List<T> list, T value){
		list.add(value);
	}
}

@Builder
class B{
	@Singular List<String> blists;
}

@Builder
class D{
	@Singular List<Integer> dlists;
}