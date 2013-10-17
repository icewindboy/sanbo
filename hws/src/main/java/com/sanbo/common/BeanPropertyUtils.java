package com.sanbo.common;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;


public class BeanPropertyUtils {

	public static <T> T copyProperties(T dest,T orig,String... properties){
		return copyProperties(dest, orig,false, properties);
	}
	
	public static <T> T copyProperties(T dest,T orig,boolean updateNull,String... properties){
		for(String property:properties){
			try {
				Object value = PropertyUtils.getProperty(orig, property);
				if(value!=null || updateNull){
					PropertyUtils.setProperty(dest, property, value);
				}
			} catch (Exception e) {
				//do nothing
			} 
		}
		return dest;
	}

	public static <T> T copyProperties(T dest,T orig,boolean updateNull){
		PropertyDescriptor[] pds=BeanUtils.getPropertyDescriptors(dest.getClass());
		String[] properties=new String[pds.length];
		int i=0;
		for(PropertyDescriptor pd:pds){
			properties[i++]=pd.getName();
		}
		return copyProperties(dest, orig, updateNull,properties);
	}

	public static <T> T copyProperties(T dest,T orig){
		return copyProperties(dest, orig, false);
	}
	
	
}
