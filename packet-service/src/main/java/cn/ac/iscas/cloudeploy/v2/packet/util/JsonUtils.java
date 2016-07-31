package cn.ac.iscas.cloudeploy.v2.packet.util;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	public static <T> T convertToObject(String json, Class<T> clazz) throws IOException{
		ObjectMapper maper = new ObjectMapper();
		T result = null;
		try {
			result = maper.readValue(json, clazz);
		} catch (IOException e) {
			logger.error("convert json:{} to classtype:{} failed", json, clazz);
			throw e;
		}
		return result;
	}
	
	public static String convertToJson(Object object) throws JsonProcessingException{
		ObjectMapper maper = new ObjectMapper();
		return maper.writeValueAsString(object);
	}
	
	public static <T> List<T> convertToList(String json, TypeReference<List<T>> type) throws IOException{
		ObjectMapper maper = new ObjectMapper();
		List<T> result = null;
		try {
			result = maper.readValue(json, type);
		} catch (IOException e) {
			logger.error("convert json:{} to classtype:{} failed", json, type);
			throw e;
		}
		return result;
	}
}
