package com.sanbo.common;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


public class JSONUtils {
	private JSONUtils(){}
	

	public static Map<String,Object> toMap(String jsonString){
		return toMap(jsonString, null);
	}
	
	
	
	//{1:{pid:1,qid:1},2:{pid:2,qid:2}}
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> toMap(String jsonString,final Class<T> valueClazz){
		
		JSONObject json= JSONObject.fromObject(jsonString);
		JsonConfig config=new JsonConfig();
		config.setRootClass(Map.class);
		if(valueClazz!=null){
			HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();
			classMap.put(".*", valueClazz);
			config.setClassMap(classMap);
		}
		return (Map<String,T>)JSONObject.toBean(json,config);
	}
	

	
	public static <T> String toJSON(Map<String,T> obj){
		return JSONObject.fromObject(obj).toString();
	}
	
	
	
}
