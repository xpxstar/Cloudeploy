package cn.ac.iscas.cloudeploy.v2.model.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Meta {
	@Getter @Setter private String name;
	@Getter @Setter private String type;
	@Getter @Setter private String component;
	@Getter @Setter private String author;
	@Getter @Setter private Long version;
	@Getter @Setter private List<String> tags;
	@Getter @Setter private String summary;
	@Getter @Setter private List<Dependency> dependencies;
	@Getter @Setter private List<Dependency> os_support;
	@Getter @Setter private List<Dependency> requirements;
	@Getter @Setter private String path;
	
}
