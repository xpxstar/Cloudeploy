package cn.ac.iscas.cloudeploy.v2.model.entity;

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
public class Dependency {
	@Getter @Setter private String name;
	@Getter @Setter private Long upversion;
	@Getter @Setter private Long downversion;
}
