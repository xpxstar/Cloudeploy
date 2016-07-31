package cn.ac.iscas.cloudeploy.v2.controller.dataview.resource;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.resource.file.CustomFile;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.file.RemoteFile;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class FileResourceViews {
	private static class BasicFileItem {
		public Long id;
		public Long createdAt;
		public Long updatedAt;
		public String name;
	}

	/**
	 * custom file section
	 * 
	 * @author gaoqiang
	 *
	 */

	public static class CustomFileItem extends BasicFileItem {
		public String fileKey;
	}

	private static Function<CustomFile, CustomFileItem> CUSTOM_ITEM_VIEW_TRANSFORMER = new Function<CustomFile, CustomFileItem>() {
		@Override
		public CustomFileItem apply(CustomFile input) {
			if (input == null)
				return null;
			CustomFileItem view = new CustomFileItem();
			view.id = input.getId();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			view.fileKey = input.getFileMd5();
			view.name = input.getName();
			return view;
		}
	};

	public static CustomFileItem customViewOf(CustomFile input) {
		return input == null ? null : CUSTOM_ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<CustomFileItem> customListViewOf(List<CustomFile> input) {
		return input == null ? ImmutableList.<CustomFileItem> of() : Lists
				.transform(input, CUSTOM_ITEM_VIEW_TRANSFORMER);
	}

	/**
	 * remote file section
	 * 
	 * @author gaoqiang
	 *
	 */

	public static class RemoteFileItem extends BasicFileItem {
		public String path;
		public Long hostId;
	}

	private static Function<RemoteFile, RemoteFileItem> REMOTE_ITEM_VIEW_TRANSFORMER = new Function<RemoteFile, RemoteFileItem>() {
		@Override
		public RemoteFileItem apply(RemoteFile input) {
			if (input == null)
				return null;
			RemoteFileItem view = new RemoteFileItem();
			view.id = input.getId();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			view.path = input.getPath();
			view.name = input.getName();
			view.hostId = input.getHost().getId();
			return view;
		}
	};

	public static RemoteFileItem remoteViewOf(RemoteFile input) {
		return input == null ? null : REMOTE_ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<RemoteFileItem> remoteListViewOf(List<RemoteFile> input) {
		return input == null ? ImmutableList.<RemoteFileItem> of() : Lists
				.transform(input, REMOTE_ITEM_VIEW_TRANSFORMER);
	}

}
