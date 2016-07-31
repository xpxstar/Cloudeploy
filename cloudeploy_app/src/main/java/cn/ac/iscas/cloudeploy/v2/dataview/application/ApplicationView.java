package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ApplicationView {
	public static class Item {
		public Long id;
		public String name;
		public Application.Status status;
		public Long createdAt;
		public Long updatedAt;
	}
	
	public static class DetailedItem extends Item {
		public List<ContainerView.DetailedItem> containers;
		public List<RelationView.Item> relations;
	}

	private static Function<Application, Item> ITEM_VIEW_TRANSFORMER = new Function<Application, Item>() {
		@Override
		public Item apply(Application input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.status = input.getStatus();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			return view;
		}
	};

	public static Item viewOf(Application input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<Application> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}

	private static Function<Application, DetailedItem> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<Application, DetailedItem>() {
		@Override
		public DetailedItem apply(Application input) {
			if (input == null)
				return null;
			DetailedItem view = new DetailedItem();
			view.id = input.getId();
			view.name = input.getName();
			view.status = input.getStatus();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			view.containers = ContainerView.detailedViewListOf(input
					.getContainers());
			view.relations = RelationView.viewListOf(input.getRelations());
			return view;
		}
	};

	public static DetailedItem detailedViewOf(Application input) {
		return input == null ? null : DTAILED_ITEM_VIEW_TRANSFORMER
				.apply(input);
	}

	public static List<DetailedItem> detailedViewListOf(List<Application> input) {
		return input == null ? ImmutableList.<DetailedItem> of() : Lists
				.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER);
	}
}
