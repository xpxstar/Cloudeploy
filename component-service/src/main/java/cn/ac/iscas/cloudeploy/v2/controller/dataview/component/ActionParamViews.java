package cn.ac.iscas.cloudeploy.v2.controller.dataview.component;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ActionParamViews {
	public static class Item {
		public Long id;
		public String paramKey;
		public String defaultValue;
		public String description;
	}

	private static Function<ActionParam, Item> ITEM_VIEW_TRANSFORMER = new Function<ActionParam, Item>() {
		@Override
		public Item apply(ActionParam input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.paramKey = input.getParamKey();
			view.defaultValue = input.getDefaultValue();
			view.description = input.getDescription();
			return view;
		}
	};

	public static Item viewOf(ActionParam input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> listViewOf(List<ActionParam> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
