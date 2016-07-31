package cn.ac.iscas.cloudeploy.v2.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

@Service
public class DefaultConfigServiceImpl implements ConfigService {

	private Config config, serviceConfig, paramConfig;

	@PostConstruct
	private void postConstruct() {
		config = ConfigFactory.load("configs.properties");
		serviceConfig = ConfigFactory.load("service.json");
		paramConfig = ConfigFactory.load("params.json");
	}

	@Override
	public int getConfigAsInt(String key) {
		return config.getInt(key);
	}

	@Override
	public String getConfigAsString(String key) {
		return config.getString(key);
	}

	@Override
	public JSONObject getServiceConfigAsJSONObject(String serviceKey) {
		ConfigRenderOptions options = ConfigRenderOptions.concise();
		return new JSONObject(serviceConfig.getObject(serviceKey).render(
				options));
	}

	@Override
	public List<String> getParamConfigAsString(String actionPath) {
		return paramConfig.getStringList(actionPath);
	}

	public static void main(String[] args) {
		DefaultConfigServiceImpl configService = new DefaultConfigServiceImpl();
		configService.postConstruct();
		System.out.println(configService.getParamConfigAsString("exec.run"));
	}

	@Override
	public String getConfigAsString(String configFileName, String key) {
		Config config = ConfigFactory.load(configFileName);
		return config.getString(key);
	}
}
