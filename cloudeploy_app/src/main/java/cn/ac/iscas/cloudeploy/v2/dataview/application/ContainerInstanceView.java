package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance.Status;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ContainerInstanceView {
	public static class Item {
		public Long id;
		public Integer port;
		public Integer seq;
		public String name;
		public Status status;
		public Integer xPos;
		public Integer yPos;
	}

	private static Function<ContainerInstance, Item> ITEM_VIEW_TRANSFORMER = new Function<ContainerInstance, Item>() {
		@Override
		public Item apply(ContainerInstance input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.port = input.getPort();
			view.xPos = input.getxPos();
			view.yPos = input.getyPos();
			view.status = input.getStatus();
			view.seq = input.getSeq();
			return view;
		}
	};

	public static Item viewOf(ContainerInstance input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<ContainerInstance> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
