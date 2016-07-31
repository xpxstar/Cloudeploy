package cn.ac.iscas.cloudeploy.v2.model.entity.event;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.packet.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
public class ConfigEvent {
	@Getter @Setter private static String eventName = "config";
	@Getter @Setter private DHost dhost;
	@Getter @Setter private String instance;
	@Getter @Setter private String key;
	@Getter @Setter private String value;
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	private static class Payload{
		@Getter @Setter String eventKey;
		@Getter @Setter String eventValue;
	}
	public String getPayload() throws JsonProcessingException{
//		String component = instance.substring(0, instance.lastIndexOf("_"));
		Payload payload = Payload.builder()
			.eventKey(key)
			.eventValue(value).build();
		return JsonUtils.convertToJson(payload);
	}
}
