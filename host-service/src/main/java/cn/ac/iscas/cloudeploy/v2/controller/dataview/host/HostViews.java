package cn.ac.iscas.cloudeploy.v2.controller.dataview.host;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class HostViews {
	public static class Item {
		public Long id;
		public String hostName;
		public String hostIP;
	}

	private static Function<DHost, Item> ITEM_VIEW_TRANSFORMER = new Function<DHost, Item>() {
		@Override
		public Item apply(DHost input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.hostName = input.getHostName();
			view.hostIP = input.getHostIP();
			return view;
		}
	};

	public static Item viewOf(DHost input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> listViewOf(List<DHost> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
