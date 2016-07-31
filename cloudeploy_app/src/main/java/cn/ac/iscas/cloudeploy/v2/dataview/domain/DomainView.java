package cn.ac.iscas.cloudeploy.v2.dataview.domain;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.domain.Domain;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainView {
	public static class Item {
		public Long id;
		public String name;
		public String ip;
	}

	private static Function<Domain, Item> ITEM_VIEW_TRANSFORMER = new Function<Domain, Item>() {
		@Override
		public Item apply(Domain input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.ip = input.getIp();
			return view;
		}
	};

	public static Item viewOf(Domain input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<Domain> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
