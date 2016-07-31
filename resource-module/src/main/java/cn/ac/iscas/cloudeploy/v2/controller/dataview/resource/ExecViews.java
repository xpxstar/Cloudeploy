package cn.ac.iscas.cloudeploy.v2.controller.dataview.resource;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.exec.Exec;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ExecViews {
	public static class ExecItem {
		public Long id;
		public Long createdAt;
		public Long updatedAt;
		public String name;
		public String content;
	}

	private static Function<Exec, ExecItem> EXEC_ITEM_VIEW_TRANSFORMER = new Function<Exec, ExecItem>() {
		@Override
		public ExecItem apply(Exec input) {
			if (input == null)
				return null;
			ExecItem view = new ExecItem();
			view.id = input.getId();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			view.content = input.getContent();
			view.name = input.getName();
			return view;
		}
	};

	public static ExecItem viewOf(Exec input) {
		return input == null ? null : EXEC_ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<ExecItem> listViewOf(List<Exec> input) {
		return input == null ? ImmutableList.<ExecItem> of() : Lists.transform(
				input, EXEC_ITEM_VIEW_TRANSFORMER);
	}
	
	public static class ParamItem {
		public String paramKey;
		public String paramValue;
	}
	
	public static class RunParams{
		public Long hostId;
		public List<ParamItem> params;
	}

}
