package cn.ac.iscas.cloudeploy.v2.model.entity.event;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartEvent {
	@Getter @Setter private static String eventName = "start";
	@Getter @Setter private DHost dhost;
	@Getter @Setter private String payload;
}
