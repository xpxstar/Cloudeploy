package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerParam;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ContainerParamView {
	public static class Item {
		public String key;
		public String value;
	}

	private static Function<ContainerParam, Item> ITEM_VIEW_TRANSFORMER = new Function<ContainerParam, Item>() {
		@Override
		public Item apply(ContainerParam input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.key = input.getParamKey();
			view.value = input.getParamValue();
			return view;
		}
	};

	public static Item viewOf(ContainerParam input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<ContainerParam> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
