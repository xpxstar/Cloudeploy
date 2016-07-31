package cn.ac.iscas.cloudeploy.v2.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;

public class ParamUtils {
	public static List<ActionParam> listFromParamString(String paramString){
		JSONArray params = new JSONArray(paramString);
		List<ActionParam> paramList = new ArrayList<>();
		for(int i=0;i<params.length();i++){
			JSONObject param = params.getJSONObject(i);
			ActionParam ap = new ActionParam();
			ap.setParamKey(param.optString("paramKey"));
			ap.setDefaultValue(param.optString("defaultValue"));
			ap.setDescription(param.optString("description"));
			paramList.add(ap);
		}
		return paramList;
	}
}
