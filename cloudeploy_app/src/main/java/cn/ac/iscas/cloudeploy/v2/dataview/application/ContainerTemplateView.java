package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.TemplateParam;

public class ContainerTemplateView {
	public static class Item {
		public String source;
		public String target;
		public String command;
		public Long createdAt;
		public Long updatedAt;
	}

	private static Function<TemplateParam, ContainerTemplateView.Item> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<TemplateParam, ContainerTemplateView.Item>(){
		@Override
		public Item apply(TemplateParam input) {
			ContainerTemplateView.Item view = new ContainerTemplateView.Item();
			view.source = input.getSource();
			view.target = input.getTarget();
			view.command = input.getCommand();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			return view;
		}
	};

	public static List<ContainerTemplateView.Item> viewListOf(
			List<TemplateParam> input) {
		return input == null ? ImmutableList.<ContainerTemplateView.Item> of() : Lists
				.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER );
	}
}
