package cn.ac.iscas.cloudeploy.v2.controller.dataview.component;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ActionViews {
	public static class Item {
		public Long id;
		public String name;
		public String displayName;
		public List<ActionParamViews.Item> params;
	}

	private static Function<Action, Item> ITEM_VIEW_TRANSFORMER = new Function<Action, Item>() {
		@Override
		public Item apply(Action input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.displayName = input.getDisplayName();
			view.params = ActionParamViews.listViewOf(input.getParams());
			return view;
		}
	};

	public static Item viewOf(Action input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> listViewOf(List<Action> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
