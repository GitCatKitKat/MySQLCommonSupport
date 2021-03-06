package com.mysql.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import com.mysql.entity.Person;

public class Query {
	private static ClassFactory CLASS_FACTORY; // 表名与字段名、值的实体类
	private static String TABLE_NAME; // 表名
	private static Object[] VALUES_ARRAY; // 字段值数组
	private static Object[] COLUNMS_ARRAY; // 字段名数组
	private static String VALUES_SERIALIZATION_STRING; // 字段值序列
	private static String COLUNMS_SERIALIZATION_STRING;// 字段名序列
	private static Integer FLAG;// 公用标志
	private static String SQL_STATEMENT;
	private static Object OBJECT;

	/**
	 * 
	 * @param object
	 * @param SQLHeader
	 * @param WHEREColunm
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ParseException
	 */
	public Query(Object object, String SQLHeader, String[] WHEREColunm)
			throws IllegalArgumentException, InvocationTargetException, ParseException {
		CLASS_FACTORY = new ClassFactory();
		new ObjectGetter(object, CLASS_FACTORY);
		TABLE_NAME = CLASS_FACTORY.getClassName();
		VALUES_SERIALIZATION_STRING = CLASS_FACTORY.getKeyValue().keySet().toString().substring(1,
				CLASS_FACTORY.getKeyValue().keySet().toString().length() - 1);
		COLUNMS_SERIALIZATION_STRING = CLASS_FACTORY.getKeyValue().values().toString().substring(1,
				CLASS_FACTORY.getKeyValue().values().toString().length() - 1);
		VALUES_ARRAY = CLASS_FACTORY.getKeyValue().values().toArray();
		COLUNMS_ARRAY = CLASS_FACTORY.getKeyValue().keySet().toArray();
		if (WHEREColunm != null) {
			Query.SQL_STATEMENT = Query.commonSQLGenerator(SQLHeader, WHEREColunm);
		} else {
			Query.SQL_STATEMENT = Query.commonSQLGenerator(SQLHeader);
		}
		Query.OBJECT = object;
	}

	/**
	 * 
	 * @param SQLHeader
	 * @return SQL Statement(Basic CRUD)
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static String commonSQLGenerator(String SQLHeader)
			throws IllegalArgumentException, InvocationTargetException {

		// StringBuffer 实例用于储存SQL语句
		StringBuffer stringBuffer = new StringBuffer();
		// 语句HEADER以判断生成何种语句
		String header = SQLHeader.toUpperCase();

		if (header.startsWith("INSERT")) {
			stringBuffer.append("INSERT INTO " + TABLE_NAME + " (" + VALUES_SERIALIZATION_STRING + ") VALUES ("
					+ COLUNMS_SERIALIZATION_STRING + ")");
		} else if (header.startsWith("SELECT")) {
			stringBuffer.append("SELECT " + VALUES_SERIALIZATION_STRING + " FROM " + TABLE_NAME);
			// 初始化FLAG（判断VALUES_ARRAY是否为空的标志）
			FLAG = 0;
			// 如果数组中有一个元素不为空，则flag+1
			for (int i = 0; i < VALUES_ARRAY.length; i++) {
				if (VALUES_ARRAY[i] != null) {
					FLAG++;
				}
			}
			// flag !=0，说明数组中存在值，则启动WHERE条件
			if (FLAG != 0) {
				String whereClause = " WHERE ";
				for (int i = 0; i < COLUNMS_ARRAY.length; i++) {
					// 判断不为空的属性才出现在WHERE条件中
					if (VALUES_ARRAY[i] != null) {
						// 值的开头为'%则启动LIKE条件，否则皆为=
						if (VALUES_ARRAY[i].toString().startsWith("'%")) {
							whereClause += COLUNMS_ARRAY[i] + " LIKE " + VALUES_ARRAY[i] + " AND ";
						} else {
							whereClause += COLUNMS_ARRAY[i] + "=" + VALUES_ARRAY[i] + " AND ";
						}
					}
				}
				// 去掉结尾的 AND
				stringBuffer.append(whereClause.substring(0, whereClause.length() - 4));
			}

		} else if (header.startsWith("DELETE")) {
			// 初始化FLAG（判断VALUES_ARRAY是否为空的标志）
			FLAG = 0;
			// 如果数组中有一个元素不为空，则flag+1
			for (int i = 0; i < VALUES_ARRAY.length; i++) {
				if (VALUES_ARRAY[i] != null) {
					FLAG++;
				}
			}
			// flag !=0，说明数组中存在值，并且数组第一个属性(一般是ID)不为空，生成语句，否则打印错误
			if (FLAG != 0) {
				stringBuffer.append("DELETE FROM " + TABLE_NAME + " WHERE ");
				String whereClause = "";
				for (int i = 0; i < COLUNMS_ARRAY.length; i++) {
					if (VALUES_ARRAY[i] != null) {
						whereClause += COLUNMS_ARRAY[i] + "=" + VALUES_ARRAY[i] + ", ";
					}
				}
				stringBuffer.append(whereClause.substring(0, whereClause.length() - 2));
			} else {
				System.err.println(new Exception("没有条件，无法删除"));
			}

		}
		System.out.println("【为您生成的SQL语句为】：" + stringBuffer.toString().toUpperCase());
		return stringBuffer.toString().toUpperCase();
	}

	/**
	 * 
	 * @param  SQLHeader
	 * @param  WHEREColunm
	 * @return SQL Statement(Basic CRUD)
	 */
	public static String commonSQLGenerator(String SQLHeader, String[] WHEREColunm) {
		// StringBuffer 实例用于储存SQL语句
		StringBuffer stringBuffer = new StringBuffer();
		// 语句HEADER以判断生成何种语句
		String header = SQLHeader.toUpperCase();

		if (header.startsWith("UPDATE")) {
			// 初始化FLAG（判断VALUES_ARRAY是否为空的标志）
			FLAG = 0;
			// 如果数组中有一个元素不为空，则flag+1
			for (int i = 0; i < VALUES_ARRAY.length; i++) {
				if (VALUES_ARRAY[i] != null) {
					FLAG++;
				}
			}
			// flag !=0，说明数组中存在值，生成语句，否则打印错误
			if (FLAG != 0) {
				stringBuffer.append("UPDATE " + TABLE_NAME + " SET ");
				String keyValues = "";
				String whereClause = "";
				for (int i = 0; i < COLUNMS_ARRAY.length; i++) {
					if (VALUES_ARRAY[i] != null) {
						keyValues += COLUNMS_ARRAY[i] + " = " + VALUES_ARRAY[i] + " ,";
					}
				}
				for (int i = 0; i < COLUNMS_ARRAY.length; i++) {
					if (VALUES_ARRAY[i] != null) {
						for (int j = 0; j < WHEREColunm.length; j++) {
							if (COLUNMS_ARRAY[i].toString().toUpperCase()
									.equals(WHEREColunm[j].toString().toUpperCase())) {
								whereClause += WHEREColunm[j] + " = " + VALUES_ARRAY[i] + " AND ";
							}
						}
					}
				}
				stringBuffer.append(keyValues.substring(0, keyValues.length() - 1) + "WHERE "
						+ whereClause.substring(0, whereClause.length() - 4));
			} else {
				System.err.println(new Exception("没有条件，无法更新"));
			}

		}
		System.out.println("【为您生成的SQL语句为】：" + stringBuffer.toString().toUpperCase());
		return stringBuffer.toString().toUpperCase();
	}

	/**
	 * 数据库改动操作
	 * @return
	 */
	public Integer executeUpdate() {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement(SQL_STATEMENT);
			Integer result = ps.executeUpdate(SQL_STATEMENT);
			conn.close();
			ps.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 数据库查询操作
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public <T> List<T> executeSelect() throws ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 返回所用的实例
		List<T> list = new ArrayList<T>();
		// 从数据库中查询的列名集合
		List<String> colunms = new ArrayList<String>();
		// 获取被查询的实体字节码
		Class<?> clz = Class.forName(OBJECT.getClass().getName());
		Connection conn = DBConnection.getConnection();
		PreparedStatement ps = conn.prepareStatement(SQL_STATEMENT);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		// 获取列名存入集合
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			colunms.add(rsmd.getColumnName(i + 1));
		}
		// 循环结果集
		while (rs.next()) {
			@SuppressWarnings("unchecked")
			// 每一条结果集，实例化一组对象
			T obj = (T) clz.newInstance();
			// 循环列名集合从而拼合setter方法名
			for (int i = 0; i < colunms.size(); i++) {
				String colunm = colunms.get(i);
				String setMethod = "set" + colunm.substring(0, 1).toUpperCase() + colunm.substring(1);
				// 获取被查询对象的所有方法
				Method[] methods = obj.getClass().getMethods();
				// 循环方法集合
				for (int j = 0; j < methods.length; j++) {
					// 获取其中一个方法
					Method method = methods[j];
					// 判断这个方法是不是属性的setter方法
					if (method.getName().equals(setMethod)) {
						// 反执行setter方法，把RS结果集中的值封装到实体
						method.invoke(obj, rs.getObject(colunm));
						// 跳出循环
						break;
					}
				}
			}
			list.add(obj);
		}
		conn.close();
		ps.close();
		rs.close();
		return list;
	}

}
