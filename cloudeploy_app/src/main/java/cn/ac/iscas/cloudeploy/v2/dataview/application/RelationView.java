package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Relation;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class RelationView {
	public static class Item {
		public Long id;
		public String from;
		public String to;
	}

	private static Function<Relation, Item> ITEM_VIEW_TRANSFORMER = new Function<Relation, Item>() {
		@Override
		public Item apply(Relation input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.from = input.getFrom().getIdentifier();
			view.to = input.getTo().getIdentifier();
			return view;
		}
	};

	public static Item viewOf(Relation input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<Relation> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
