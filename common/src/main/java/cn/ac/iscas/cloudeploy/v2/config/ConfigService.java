package cn.ac.iscas.cloudeploy.v2.config;

import java.util.List;

import org.json.JSONObject;

public interface ConfigService {
	public int getConfigAsInt(String key);

	public String getConfigAsString(String key);

	public JSONObject getServiceConfigAsJSONObject(String serviceKey);

	public List<String> getParamConfigAsString(String actionPath);

	public String getConfigAsString(String configFileName, String key);
}
