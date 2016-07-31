package cn.ac.iscas.cloudeploy.v2.util.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.orbitz.consul.Consul;

@Configuration
public class BeanConfiguration {
	@Bean
	@Profile("production")
	public Consul consul(@Value("#{configs['consul_host']}") String host, @Value("#{configs['consul_port']}") int port){
		System.err.println(host+":"+port);
		return Consul.newClient(host, port);
	}
//	@Bean
//	@Profile("production")
//	public TypeTransfer devScriptsService(){
//		return new TypeTransfer(){
//			private List<TypeImplemention> tomcatImplementions = new ArrayList<>();;
//			private List<TypeImplemention> mysqlImplementions = new ArrayList<>();
//			@PostConstruct
//			private void init(){
//				TypeImplementionBuilder builder = TypeImplemention.builder();
//				/*
//				 * build for tomcat test
//				 */
//				tomcatImplementions.add(builder.definedFile("1787f3957bbc22c7f240d0f9e034ed1d")
//						.interfaceFilename("install").paramSpecific("").build());
//				tomcatImplementions.add(builder.definedFile("e0fadd4b8e06fa44a23829d2017ab6bb")
//						.interfaceFilename("delete").paramSpecific("").build());
//				tomcatImplementions.add(builder.definedFile("693cbb8f9edc33277db3938ffecc7836")
//						.interfaceFilename("start").paramSpecific("").build());
//				tomcatImplementions.add(builder.definedFile("693cbb8f9edc33277db3938ffecc7836")
//						.interfaceFilename("stop").paramSpecific("").build());
//				/*
//				 * build for mysql test
//				 */
//				mysqlImplementions.add(builder.definedFile("d65f387df60f0896ee61f9828e02fe64")
//						.interfaceFilename("install").paramSpecific("").build());
//				mysqlImplementions.add(builder.definedFile("a09b15810fb013aa2d7d7e3f4c7f8568")
//						.interfaceFilename("delete").paramSpecific("").build());
//				mysqlImplementions.add(builder.definedFile("36b424548e072dbbffb5f58bbfc5f913")
//						.interfaceFilename("start").paramSpecific("").build());
//				mysqlImplementions.add(builder.definedFile("36b424548e072dbbffb5f58bbfc5f913")
//						.interfaceFilename("stop").paramSpecific("").build());
//			}
//			@Override
//			public List<TypeImplemention> getTypeImplementions(PacketStrategy vmpuppet, String nodeType) {
//				Preconditions.checkArgument(!Strings.isNullOrEmpty(nodeType),"nodetype can't be null or empty");
//				
//				switch(vmpuppet){
//				case VMPUPPET:
//					if(nodeType.contains("tomcat")) return tomcatImplementions;
//					else if(nodeType.contains("mysql")) return mysqlImplementions;
//					break;
//				}
//				return Collections.emptyList();
//			}
//		};
//	}
}
