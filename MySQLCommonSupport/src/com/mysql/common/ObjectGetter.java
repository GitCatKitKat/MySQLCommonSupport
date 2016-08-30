package com.mysql.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObjectGetter {
	
	/**
	 * 对象反射操作，向ClassFactory实体插入列名与值
	 * @param object
	 * @param classFactory
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ParseException
	 */
	public ObjectGetter(Object object, ClassFactory classFactory)
			throws IllegalArgumentException, InvocationTargetException, ParseException {

		Map<String, Object> objectMap = new HashMap<String, Object>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		System.out.println("MySQLCommonSupport Service 正在为您获取对象....");
		System.out.println("Powered by 冯心睿");
		// 1.得到参数对象的字节码
		Class<? extends Object> clz = object.getClass();
		// 2.获得参数对象中的所有方法
		Method[] methods = clz.getMethods();
		// 4.得到类名并输出控制台
		String clzName = clz.getName();
		// 5.表名
		String tableName = clzName.split("\\.")[clzName.split("\\.").length - 1];

		for (Method method : methods) {
			
			// 获取每个方法的名称
			String methodName = method.getName();
			// 获取所有get方法，但不包括getClass
			if (methodName.startsWith("get") && !methodName.startsWith("getClass")) {
				// 获取字段名
				String fieldName = methodName.substring(3, methodName.length());
				System.out.println("属性名:" + fieldName);
				// 判断属性的值是否为字符串，否则不加''
				Object value = null;
				try {
					value = method.invoke(object, null);
					if (value instanceof String) {
						objectMap.put(fieldName, "'" + value + "'");
					} else if(value instanceof Date){
						objectMap.put(fieldName, "'" + formatter.format(value) + "'");
						System.out.println(formatter.format(value));
					}else{
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
