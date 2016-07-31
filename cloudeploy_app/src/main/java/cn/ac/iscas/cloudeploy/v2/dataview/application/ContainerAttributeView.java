package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerAttribute;

public class ContainerAttributeView {
	public static class Item{
		public String attrKey;
		public String attrValue;
	}

	private static Function<ContainerAttribute, Item> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<ContainerAttribute, ContainerAttributeView.Item>(){
		@Override
		public Item apply(ContainerAttribute input) {
			Item view = new Item();
			view.attrKey = input.getAttrKey();
			view.attrValue = input.getAttrValue();
			return view;
		}
		
	};

	public static List<Item> viewListOf(
			List<ContainerAttribute> input) {
		return input == null ? ImmutableList.<Item> of() : Lists
				.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER  );
	}
}
