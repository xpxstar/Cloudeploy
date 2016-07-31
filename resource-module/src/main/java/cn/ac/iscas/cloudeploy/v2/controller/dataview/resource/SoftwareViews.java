package cn.ac.iscas.cloudeploy.v2.controller.dataview.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.InstanceParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.software.SoftwareInstance;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SoftwareViews {
	public static class BaseInstanceItem {
		public Long id;
		public String alias;
		public String status;
		public String softwareName;
		public Long createdAt;
		public Long updatedAt;
	}

	/**
	 * 某个instance的概要信息
	 * 
	 * @author gaoqiang
	 *
	 */
	public static class InstanceItem extends BaseInstanceItem {
		public List<ActionItem> actions;
	}

	private static Function<SoftwareInstance, InstanceItem> INSTANCE_ITEM_VIEW_TRANSFORMER = new Function<SoftwareInstance, InstanceItem>() {
		@Override
		public InstanceItem apply(SoftwareInstance input) {
			if (input == null)
				return null;
			InstanceItem view = new InstanceItem();
			view.id = input.getId();
			view.alias = input.getAlias();
			view.status = input.getStatus().name();
			view.actions = actionListViewOf(input.getComponent().getActions());
			view.softwareName = input.getComponent().getDisplayName();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			return view;
		}
	};

	public static InstanceItem instanceViewOf(SoftwareInstance input) {
		return input == null ? null : INSTANCE_ITEM_VIEW_TRANSFORMER
				.apply(input);
	}

	public static List<InstanceItem> instanceListViewOf(
			List<SoftwareInstance> input) {
		return input == null ? ImmutableList.<InstanceItem> of() : Lists
				.transform(input, INSTANCE_ITEM_VIEW_TRANSFORMER);
	}

	/**
	 * action 概要信息
	 * 
	 * @author gaoqiang
	 *
	 */
	public static class ActionItem {
		public Long id;
		public String name;
		public String displayName;
		public Boolean showInResourceView;
	}

	private static Function<Action, ActionItem> ACTION_ITEM_VIEW_TRANSFORMER = new Function<Action, ActionItem>() {
		@Override
		public ActionItem apply(Action input) {
			if (input == null)
				return null;
			ActionItem view = new ActionItem();
			view.id = input.getId();
			view.name = input.getName();
			view.displayName = input.getDisplayName();
			view.showInResourceView = input.getShowInResourceView();
			return view;
		}
	};

	public static ActionItem actionViewOf(Action input) {
		return input == null ? null : ACTION_ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<ActionItem> actionListViewOf(List<Action> input) {
		return input == null ? ImmutableList.<ActionItem> of() : Lists
				.transform(input, ACTION_ITEM_VIEW_TRANSFORMER);
	}

	public static class BasicParamItem {
		public String paramKey;
		public String paramValue;
	}

	/**
	 * 接收request post参数
	 * 
	 * @author gaoqiang
	 *
	 */
	public static class BasicInstanceParams {
		public List<BasicParamItem> params;
	}

	/**
	 * 接收软件安装
	 * 
	 * @author gaoqiang
	 *
	 */
	public static class InstanceParams extends BasicInstanceParams {
		public Long actionId;
		public Long hostId;
		public String alias;
	}

	/**
	 * 带描述的参数
	 * 
	 * @author gaoqiang
	 *
	 */
	public static class DescribedParamItem extends BasicParamItem {
		public String description;
		public String viewType;
	}

	/**
	 * 带有相应action参数的软件实例instance的信息
	 * 
	 * @author gaoqiang
	 *
	 */
	public static class ParameteredInstanceItem extends BaseInstanceItem {
		public ParameteredInstanceItem() {
			super();
			this.params = ImmutableList.<DescribedParamItem> of();
		}

		public ParameteredInstanceItem(SoftwareInstance instance, Action action) {
			this();
			this.id = instance.getId();
			this.alias = instance.getAlias();
			this.status = instance.getStatus().name();
			this.softwareName = instance.getComponent().getDisplayName();
			this.createdAt = instance.getCreateTime().getTime();
			this.updatedAt = instance.getUpdateTime().getTime();
			Map<String, DescribedParamItem> paramCache = new HashMap<>();
			for (ActionParam param : action.getParams()) {
				DescribedParamItem item = new DescribedParamItem();
				item.paramKey = param.getParamKey();
				item.paramValue = param.getDefaultValue();
				item.description = param.getDescription();
				item.viewType = param.getViewType();
				paramCache.put(item.paramKey, item);
			}
			for (InstanceParam param : instance.getParams()) {
				if (paramCache.containsKey(param.getParamKey())) {
					paramCache.get(param.getParamKey()).paramValue = param
							.getParamValue();
				}
			}
			params = new ArrayList<>();
			params.addAll(paramCache.values());
		}

		public List<DescribedParamItem> params;
	}
}
