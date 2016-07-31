package cn.ac.iscas.cloudeploy.v2.dataview.application;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.dataview.application.PropertyView.PropertyItem;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;

public class PropertyView {
	public static class PropertyItem{
		public Long appId;
		public Long containerId;
		public String key;
		public String value;
	}
	public List<PropertyItem> properties;
	
	private static Function<AttributeElement, PropertyItem> ATTRIBUTES_VIEW_TRANSFORMER = 
			new Function<AttributeElement, PropertyItem>(){
				@Override
				public PropertyItem apply(AttributeElement input) {
					if(input == null)
						return null;
					PropertyItem view = new PropertyItem();
					view.key = input.getName();
					view.value = input.getValue();
					return view;
				}
	};
	private static Function<PropertyElement, PropertyItem> PROPERTY_VIEW_TRANSFORMER = 
			new Function<PropertyElement, PropertyView.PropertyItem>() {
				@Override
				public PropertyItem apply(PropertyElement input) {
					if(input == null)
						return null;
					PropertyItem view = new PropertyItem();
					view.key = input.getName();
					view.value = input.getDefaultValue();
					return view;
				}
			};
			
	
	public static List<PropertyItem> viewListOf(List<PropertyElement> input, Long appId, Long containerId){
		if(input == null)
			return ImmutableList.<PropertyView.PropertyItem> of();
		else{
			List<PropertyItem> result = new ArrayList<>(Lists.transform(input, PROPERTY_VIEW_TRANSFORMER));
			for(PropertyItem item : result){
				item.appId = appId;
				item.containerId = containerId;
			};
			return result;
		}
	}


	public static List<PropertyItem> viewListOfArrtibutes(List<AttributeElement> attributes, Long appId, Long containerId) {
		if(attributes == null)
			return ImmutableList.<PropertyView.PropertyItem> of();
		else{
			List<PropertyItem> result = new ArrayList<>(Lists.transform(attributes, ATTRIBUTES_VIEW_TRANSFORMER));
			for(PropertyItem item : result){
				item.appId = appId;
				item.containerId = containerId;
			};
			return result;
		}
	}
}
