package com.mysql.common;

import java.lang.reflect.Method;
import java.util.Map;

public class ClassFactory {
	private Map<String,Object> keyValue;
	private String className;
	private Method[] methods;
	
	
	
	public Method[] getMethods() {
		return methods;
	}
	public void setMethods(Method[] methods) {
		this.methods = methods;
	}
	public Map<String, Object> getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(Map<String, Object> keyValue) {
		this.keyValue = keyValue;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
}
