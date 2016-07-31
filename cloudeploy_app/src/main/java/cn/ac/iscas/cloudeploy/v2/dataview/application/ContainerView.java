package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container.Status;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ContainerView {
	public static class Item {
		public String name;
		public int port;
		public Integer xPos;
		public Integer yPos;
		public Long masterId;
		public int maxCount;
		public int initCount;
		public Long componentId;
		public String nodeId;
		public Long id;
		public Status status;
	}

	public static class DetailedItem extends Item {
		public List<ContainerParamView.Item> params;
		public List<ContainerTemplateView.Item> templates;
		public List<ContainerAttributeView.Item> attributes;
		public List<ContainerInstanceView.Item> instances;
	}

	private static Function<Container, Item> ITEM_VIEW_TRANSFORMER = new Function<Container, Item>() {
		@Override
		public Item apply(Container input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.port = input.getPort();
			view.xPos = input.getxPos();
			view.yPos = input.getyPos();
			view.status = input.getStatus();
			view.maxCount = input.getMaxCount();
			view.initCount = input.getInitCount();
			view.componentId = input.getComponent() == null ? null : input
					.getComponent().getId();
			view.nodeId = input.getIdentifier();
			return view;
		}
	};

	public static Item viewOf(Container input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<Container> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}

	private static Function<Container, DetailedItem> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<Container, DetailedItem>() {
		@Override
		public DetailedItem apply(Container input) {
			if (input == null)
				return null;
			DetailedItem view = new DetailedItem();
			view.id = input.getId();
			view.name = input.getName();
			view.port = input.getPort();
			view.xPos = input.getxPos();
			view.yPos = input.getyPos();
			view.maxCount = input.getMaxCount();
			view.initCount = input.getInitCount();
			view.componentId = input.getComponent() == null ? null : input
					.getComponent().getId();
			view.params = ContainerParamView.viewListOf(input.getParams());
			view.templates = ContainerTemplateView.viewListOf(input.getTemplates());
			view.attributes = ContainerAttributeView.viewListOf(input.getAttributes());
			view.instances = ContainerInstanceView.viewListOf(input
					.getInstances());
			view.nodeId = input.getIdentifier();
			view.status = input.getStatus();
			return view;
		}
	};

	public static DetailedItem detailedViewOf(Container input) {
		return input == null ? null : DTAILED_ITEM_VIEW_TRANSFORMER
				.apply(input);
	}

	public static List<DetailedItem> detailedViewListOf(List<Container> input) {
		return input == null ? ImmutableList.<DetailedItem> of() : Lists
				.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER);
	}
}
