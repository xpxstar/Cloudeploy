package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "d_node_packet")
@Builder(builderClassName="NtmplPacketEntityBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class NodeTemplatePacket extends IdEntity{
	@Column
	@Getter @Setter private String templateName;
	@Column
	@Getter @Setter private String packetMd5;
	@Enumerated(EnumType.STRING)
	@Getter @Setter private PacketStrategy packetStrategy;
}
