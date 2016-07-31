package cn.ac.iscas.cloudeploy.v2.packet.type.docker;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
public class ImageInfo {
	@Getter
	@Setter
	@JsonProperty("Id")
	private String id;
	@Getter
	@Setter
	@JsonProperty("Parent")
	private String parent;
	@Getter
	@Setter
	@JsonProperty("Comment")
	private String comment;
	@Getter
	@Setter
	@JsonProperty("Created")
	private Date created;
	@Getter
	@Setter
	@JsonProperty("Container")
	private String container;
	@Getter
	@Setter
	@JsonProperty("ContainerConfig")
	private ContainerConfig containerConfig;
	@Getter
	@Setter
	@JsonProperty("DockerVersion")
	private String dockerVersion;
	@Getter
	@Setter
	@JsonProperty("Author")
	private String author;
	@Getter
	@Setter
	@JsonProperty("Config")
	private ContainerConfig config;
	@Getter
	@Setter
	@JsonProperty("Architecture")
	private String architecture;
	@Getter
	@Setter
	@JsonProperty("Os")
	private String os;
	@Getter
	@Setter
	@JsonProperty("Size")
	private Long size;
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
	public static class ContainerConfig {
		@Getter
		@Setter
		@JsonProperty("Hostname")
		private String hostname;
		@Getter
		@Setter
		@JsonProperty("Domainname")
		private String domainname;
		@Getter
		@Setter
		@JsonProperty("User")
		private String user;
		@Getter
		@Setter
		@JsonProperty("Memory")
		private Long memory;
		@Getter
		@Setter
		@JsonProperty("MemorySwap")
		private Long memorySwap;
		@Getter
		@Setter
		@JsonProperty("CpuShares")
		private Long cpuShares;
		@Getter
		@Setter
		@JsonProperty("Cpuset")
		private String cpuset;
		@Getter
		@Setter
		@JsonProperty("AttachStdin")
		private Boolean attachStdin;
		@Getter
		@Setter
		@JsonProperty("AttachStdout")
		private Boolean attachStdout;
		@Getter
		@Setter
		@JsonProperty("AttachStderr")
		private Boolean attachStderr;
		@Getter
		@Setter
		@JsonProperty("PortSpecs")
		private List<String> portSpecs;
		@Getter
		@Setter
		@JsonProperty("ExposedPorts")
		private Set<String> exposedPorts;
		@Getter
		@Setter
		@JsonProperty("Tty")
		private Boolean tty;
		@Getter
		@Setter
		@JsonProperty("OpenStdin")
		private Boolean openStdin;
		@Getter
		@Setter
		@JsonProperty("StdinOnce")
		private Boolean stdinOnce;
		@Getter
		@Setter
		@JsonProperty("Env")
		private List<String> env;
		@Getter
		@Setter
		@JsonProperty("Cmd")
		private List<String> cmd;
		@Getter
		@Setter
		@JsonProperty("Image")
		private String image;
		@Getter
		@Setter
		@JsonProperty("Volumes")
		private List<String> volumes;
		@Getter
		@Setter
		@JsonProperty("WorkingDir")
		private String workingDir;
		@Getter
		@Setter
		@JsonProperty("Entrypoint")
		private List<String> entrypoint;
		@Getter
		@Setter
		@JsonProperty("NetworkDisabled")
		private Boolean networkDisabled;
		@Getter
		@Setter
		@JsonProperty("OnBuild")
		private List<String> onBuild;
	}
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final ImageInfo imageInfo = (ImageInfo) o;

		if (architecture != null ? !architecture.equals(imageInfo.architecture) : imageInfo.architecture != null) {
			return false;
		}
		if (author != null ? !author.equals(imageInfo.author) : imageInfo.author != null) {
			return false;
		}
		if (comment != null ? !comment.equals(imageInfo.comment) : imageInfo.comment != null) {
			return false;
		}
		if (config != null ? !config.equals(imageInfo.config) : imageInfo.config != null) {
			return false;
		}
		if (container != null ? !container.equals(imageInfo.container) : imageInfo.container != null) {
			return false;
		}
		if (containerConfig != null ? !containerConfig.equals(imageInfo.containerConfig)
				: imageInfo.containerConfig != null) {
			return false;
		}
		if (created != null ? !created.equals(imageInfo.created) : imageInfo.created != null) {
			return false;
		}
		if (dockerVersion != null ? !dockerVersion.equals(imageInfo.dockerVersion) : imageInfo.dockerVersion != null) {
			return false;
		}
		if (id != null ? !id.equals(imageInfo.id) : imageInfo.id != null) {
			return false;
		}
		if (os != null ? !os.equals(imageInfo.os) : imageInfo.os != null) {
			return false;
		}
		if (parent != null ? !parent.equals(imageInfo.parent) : imageInfo.parent != null) {
			return false;
		}
		if (size != null ? !size.equals(imageInfo.size) : imageInfo.size != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		result = 31 * result + (comment != null ? comment.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		result = 31 * result + (container != null ? container.hashCode() : 0);
		result = 31 * result + (containerConfig != null ? containerConfig.hashCode() : 0);
		result = 31 * result + (dockerVersion != null ? dockerVersion.hashCode() : 0);
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (config != null ? config.hashCode() : 0);
		result = 31 * result + (architecture != null ? architecture.hashCode() : 0);
		result = 31 * result + (os != null ? os.hashCode() : 0);
		result = 31 * result + (size != null ? size.hashCode() : 0);
		return result;
	}
}