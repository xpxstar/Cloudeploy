package cn.ac.iscas.cloudeploy.v2.packet.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecificEntity {
	@Singular @Getter @Setter private List<Service> services;
	@Singular @Getter @Setter private List<Attribute> attributes;
	@Getter @Setter private String name;
	@Singular @Getter @Setter private List<Template> templates;
	@Getter @Setter private Container container;
	@Getter @Setter private VMContainer vmContainer;
	

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Template {
		@Getter @Setter private String source;
		@Getter @Setter private String target;
		@Getter @Setter private String command;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Check {
		@Getter @Setter private String script;
		@Getter @Setter private Long interval;
		@Getter @Setter private String ttl;
		@Getter @Setter private String http;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Service {
		@Getter @Setter private String name;
		@Getter @Setter private String id;
		@Getter @Setter private String address;
		@Getter @Setter private String port;
		@Singular @Getter @Setter private List<Check> checks;
		@Getter @Setter private List<String> tags;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Attribute {
		@Getter @Setter private String key;
		@Getter @Setter private String value;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Container {
		@Builder
		@AllArgsConstructor
		@NoArgsConstructor
		public static class PortBinding {
			@Getter @Setter private String port;
			@Getter @Setter private String targetPort;
		}
		@Builder
		@AllArgsConstructor
		@NoArgsConstructor
		public static class HostConfig {
			@Singular @Getter @Setter private List<PortBinding> portBindings;
		}
		@Getter @Setter private String hostname;
		@Getter @Setter private String domainname;
		@Getter @Setter private String user;
		@Getter @Setter private Long memory;
		@Getter @Setter private Long memorySwap;
		@Getter @Setter private Long cpuShares;
		@Getter @Setter private String cpuset;
		@Getter @Setter private Boolean attachStdin;
		@Getter @Setter private Boolean attachStdout;
		@Getter @Setter private Boolean attachStderr;
		@Getter @Setter private List<String> portSpecs;
		@Getter @Setter private Set<String> exposedPorts;
		@Getter @Setter private Boolean tty;
		@Getter @Setter private Boolean openStdin;
		@Getter @Setter private Boolean stdinOnce;
		@Getter @Setter private List<String> env;
		@Getter @Setter private List<String> cmd;
		@Getter @Setter private String image;
		@Singular @Setter  private List<String> volumes;
		@Getter @Setter private String workingDir;
		@Getter @Setter private List<String> entrypoint;
		@Getter @Setter private Boolean networkDisabled;
		@Getter @Setter private List<String> onBuild;
		@Getter @Setter private HostConfig hostConfig;
		@Getter @Setter private String containerName;
		public List<String> getVolumes(){
			System.out.println(volumes);
			if(volumes == null){
				volumes = new ArrayList<>();
			}
			return volumes;
		}
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VMContainer {
		@Getter @Setter @Singular private List<Property> properties;
		@Getter @Setter @Singular private List<VMInterface> operations;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VMInterface{
		@Getter @Setter @Singular private List<Property> properties;
		@Getter @Setter private String path;
		@Getter @Setter private String name;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Property {
		@Getter @Setter private String name;
		@Getter @Setter private String value;
	}
}
