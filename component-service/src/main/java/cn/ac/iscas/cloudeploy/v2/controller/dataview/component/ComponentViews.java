package cn.ac.iscas.cloudeploy.v2.controller.dataview.component;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ComponentViews {
	public static class Item {
		public Long id;
		public String name;
		public String displayName;
		public ComponentTypeViews.Item type;
		public List<ActionViews.Item> actions;
	}

	private static Function<Component, Item> ITEM_VIEW_TRANSFORMER = new Function<Component, Item>() {
		@Override
		public Item apply(Component input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.displayName = input.getDisplayName();
			view.type = ComponentTypeViews.viewOf(input.getType());
			view.actions = ActionViews.listViewOf(input.getActions());
			return view;
		}
	};

	public static Item viewOf(Component input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> listViewOf(List<Component> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
