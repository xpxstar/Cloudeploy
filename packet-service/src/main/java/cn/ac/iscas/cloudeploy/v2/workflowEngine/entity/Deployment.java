package cn.ac.iscas.cloudeploy.v2.workflowEngine.entity;

import java.util.ArrayList;
import java.util.List;

import cn.ac.iscas.cloudeploy.v2.packet.Packet;

public class Deployment {
	private List<Packet> packets;
	public Deployment() {
		packets = new ArrayList<>();
	}
	public List<Packet> getPackets() {
		return packets;
	}
	public void setPackets(List<Packet> packets) {
		this.packets = packets;
	}
	public void addPacket(Packet packet){
		packets.add(packet);
	}
}
