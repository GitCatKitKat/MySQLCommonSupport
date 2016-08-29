package com.mysql.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

import com.mysql.entity.Person;

public class Query {
	private static ClassFactory CLASS_FACTORY; // 表名与字段名、值的实体类
	private static String TABLE_NAME; // 表名
	private static Object[] VALUES_ARRAY; // 字段值数组
	private static Object[] COLUNMS_ARRAY; // 字段名数组
	private static String VALUES_SERIALIZATION_STRING; // 字段值序列
	private static String COLUNMS_SERIALIZATION_STRING;// 字段名序列
	private static Integer FLAG;// 公用标志
	private static String SQL_HEADER;
	private static String SQL_STATEMENT;
	private static Object OBJECT;

	public Query(Object object, String SQLHeader, String[] WHEREColunm)
			throws IllegalArgumentException, InvocationTargetException {
		CLASS_FACTORY = new ClassFactory();
		new ObjectGetter(object, CLASS_FACTORY);
		TABLE_NAME = CLASS_FACTORY.getClassName();
		VALUES_SERIALIZATION_STRING = CLASS_FACTORY.getKeyValue().keySet().toString().substring(1,
				CLASS_FACTORY.getKeyValue().keySet().toString().length() - 1);
		COLUNMS_SERIALIZATION_STRING = CLASS_FACTORY.getKeyValue().values().toString().substring(1,
				CLASS_FACTORY.getKeyValue().values().toString().length() - 1);
		VALUES_ARRAY = CLASS_FACTORY.getKeyValue().values().toArray();
		COLUNMS_ARRAY = CLASS_FACTORY.getKeyValue().keySet().toArray();
		this.SQL_HEADER = SQLHeader;
		if (WHEREColunm != null) {
			this.SQL_STATEMENT = this.commonSQLGenerator(SQLHeader, WHEREColunm);
		} else {
			this.SQL_STATEMENT = this.commonSQLGenerator(SQLHeader);
		}
		this.OBJECT = object;

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

	// 执行数据改动操作
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

	// 执行数据查询操作
	public <T> List<T> executeSelect() throws ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<T> list = new ArrayList<T>();
		
		return list;
	}

	public static void main(String args[]) throws IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		Person person = new Person();
		// person.setGender("nv");
		person.setId("UUID5");
		person.setName("老蒙子56");
		person.setAge(90);
		// person.setTime(new Date());
		person.setMoney(20.0d);
		String[] lala = { "ID" };
		System.out.println(new Query(person, "update", lala).executeUpdate());
	}
}
