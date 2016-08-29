package com.mysql.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.entity.Person;

public class ObjectGetter {
	public ObjectGetter(Object object, ClassFactory classFactory)
			throws IllegalArgumentException, InvocationTargetException {

		// 存放列名和值得集合
		List values = new ArrayList();
		List<String> columns = new ArrayList<String>();
		Map<String, Object> objectMap = new HashMap<String, Object>();

		System.out.println("MySQLCommonSupport Service 正在为您获取对象....");
		// 1.得到参数对象的字节码
		Class<? extends Object> clz = object.getClass();
		// 2.获得参数对象中的所有方法
		Method[] methods = clz.getMethods();
		// 3.获得对象中所有的属性
		Field[] field = clz.getFields();
		// 4.得到类名并输出控制台
		String clzName = clz.getName();
		System.out.println("对象类名为:" + clzName);
		// 5.表名
		String tableName = clzName.split("\\.")[clzName.split("\\.").length - 1];
		System.out.println("数据库表名:" + tableName);

		for (Method method : methods) {
			int i = 0;
			// 获取每个方法的名称
			String methodName = method.getName();
			// 获取所有get方法，但不包括getClass
			if (methodName.startsWith("get") && !methodName.startsWith("getClass")) {
				// 获取字段名
				String fieldName = methodName.substring(3, methodName.length());
				System.out.println("方法名:" + methodName);
				System.out.println("属性名:" + fieldName);
				// 判断属性的值是否为字符串，否则不加''
				Object value = null;
				try {
					value = method.invoke(object, null);
					if (value instanceof String) {
						objectMap.put(fieldName, "'" + value + "'");
					} else {
						objectMap.put(fieldName, value);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}

		classFactory.setKeyValue(objectMap);
		classFactory.setClassName(tableName);
	}

}
