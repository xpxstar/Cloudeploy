package cn.ac.iscas.cloudeploy.v2.controller.dataview.component;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.ComponentType;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ComponentTypeViews {
	public static class Item {
		public Long id;
		public String name;
		public String displayName;
	}

	private static Function<ComponentType, Item> ITEM_VIEW_TRANSFORMER = new Function<ComponentType, Item>() {
		@Override
		public Item apply(ComponentType input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.displayName = input.getDisplayName();
			return view;
		}
	};

	public static Item viewOf(ComponentType input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> listViewOf(List<ComponentType> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
