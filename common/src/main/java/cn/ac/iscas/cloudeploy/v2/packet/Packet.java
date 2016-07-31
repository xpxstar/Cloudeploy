package cn.ac.iscas.cloudeploy.v2.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Packet {
	/**
	 * path of specificEntityFile, specificEntityFile is used to
	 * describe properties, attributes, templates message;
	 */
	@Getter @Setter private String specificEntityFile;
	/**
	 * md5 of this packet
	 */
	@Getter @Setter private String packet;
	
	@Getter @Setter private PacketStrategy strategy;
	
	@Getter @Setter private String appName;
	
	public static enum PacketStrategy{
		VMPUPPET,VMANSIBLE,DOCKER;
	}
}
