package com.haina.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haina.Finterface.FanInterface;
import com.haina.utils.ConnectionManager;

public class Dao{
	// 获取属性和get方法的map集合
	public <T> Map<String, Method> getGetMethods(Class<T> clazz) {
		// 获取obj的所有get方法
		Method[] allMethods = clazz.getDeclaredMethods();
		Map<String, Method> getMethods = new HashMap<>();
		for (int i = 0; i < allMethods.length; i++) {
			if (allMethods[i].getName().startsWith("get") && allMethods.length > 3) {
				getMethods.put(allMethods[i].getName().substring(3, allMethods[i].getName().length()).toLowerCase(),
						allMethods[i]);
			}
		}
		return getMethods;
	}

	// 获取所有属性名
	private <T> String[] getFieldsName(Class<T> clazz) {
		// 属性
		Field[] fields = clazz.getDeclaredFields();
		String[] fieldsName = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldsName[i] = fields[i].getName();
		}
		return fieldsName;

	}

	// insert
	public <T> void save(FanInterface<T> obj) {
		@SuppressWarnings("unchecked")
		Class<? extends FanInterface<T>> clazz =  (Class<? extends FanInterface<T>>) obj.getClass();
		// 获得表名
		String tableName = clazz.getSimpleName().toLowerCase();
		// 获得属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz);
		// 获取所有属性
		String fieldnames[] = getFieldsName(clazz);
		// 拼接sql语句
		// insert into 表名('sno','sname','sage'...) value ('','','',...)
		String sql1 = "insert into " + tableName + " (";
		String sql2 = ") values (";
		try {
			for (String s : fieldnames) {
				sql1 += s + ",";
				sql2 += "'" + getMethods.get(s).invoke(obj) + "',";
			}

			String sql = sql1.substring(0, sql1.lastIndexOf(",")) + sql2.substring(0, sql2.lastIndexOf(",")) + ")";
			System.out.println(sql);
			executeSql(sql);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	// deleteAll
	public <T> void deleteAll(FanInterface<T> obj) {
		Class<?> clazz = obj.getClass();
		String tableName = clazz.getSimpleName().toLowerCase();
		String sql1 = "delete from " + tableName;
		System.out.println(sql1);
	}

	// delete
	public <T> void delete(FanInterface<T> obj) {
		Class<?> clazz = obj.getClass();
		// 获得表名
		String tableName = clazz.getSimpleName().toLowerCase();
		String sql1 = "delete from " + tableName;
		String sql2 = " where ";
		String sql;

		// System.out.println(sql1);
		// 获得属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz);
		// 获取所有属性
		String fieldnames[] = getFieldsName(clazz);
		try {
			for (String s : fieldnames) {
				// 得到运行中的属性值
				Object getInvoke = getMethods.get(s).invoke(obj);
				if (getInvoke != null) {
					if (!getInvoke.toString().equals("0")) {
						sql2 += s + "='" + getInvoke + "' and ";
						// System.out.println(getInvoke);
					}
				}
			}
			sql = sql1 + sql2.substring(0, sql2.lastIndexOf("and"));
			System.out.println(sql);
			executeSql(sql);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	// Update
	// UPDATE student set sname='ss',sage=13 where sno=3
	public <T> void update(FanInterface<T> obj1, FanInterface<T> obj2) {
		// 原数据
		Class<?> clazz1 = obj1.getClass();
		// 要修改的数据
		Class<?> clazz2 = obj2.getClass();
		// 获得表名
		String tableName = clazz1.getSimpleName().toLowerCase();
		String sql1 = "update " + tableName + " set ";
		String sql2 = " where ";
		String sql = "";
		// 获得原数据属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz1);
		try {
			for (String s : getFieldsName(clazz1)) {
				// 修改内容条件条件
				if (getMethods.get(s).invoke(obj1) != null) {
					if (!getMethods.get(s).invoke(obj1).toString().equals("0")) {
						sql2 += s + "='" + getMethods.get(s).invoke(obj1) + "' and ";
					}
				}
			}
			for (String s : getFieldsName(clazz2)) {
				// 修改内容条件
				if (getMethods.get(s).invoke(obj2) != null) {
					if (!getMethods.get(s).invoke(obj2).toString().equals("0")) {
						sql1 += s + "='" + getMethods.get(s).invoke(obj2) + "',";
					}
				}
			}
			sql = sql1.substring(0, sql1.lastIndexOf(",")) + sql2.substring(0, sql2.lastIndexOf("and"));
			System.out.println(sql);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	// select and
	@SuppressWarnings("unchecked")
	public <T> List<T> selectand(FanInterface<T> obj) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		// 获得属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz);
		// 获取所有属性
		String fieldnames[] = getFieldsName(clazz);
		String sql1 = "select * from " + tablename;
		String sql2 = " where ";
		String sql = "";
		List<T> list = null;
		try {
			for (String s : fieldnames) {
				if (getMethods.get(s).invoke(obj) != null) {
					if (!getMethods.get(s).invoke(obj).toString().equals("0")) {
						sql2 += s + "='" + getMethods.get(s).invoke(obj) + "' and ";
					}
				}
			}
			sql = sql1 + sql2;
			if (sql.endsWith("where ")) {
				sql = sql.substring(0, sql.lastIndexOf("where "));
			} else {
				sql = sql1 + sql2.substring(0, sql2.lastIndexOf("and"));
			}
			System.out.println(sql);
			list = (List<T>) getForList(clazz, sql);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	// select or
	@SuppressWarnings("unchecked")
	public <T> List<T> selector(FanInterface<T> obj) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		// 获得属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz);
		// 获取所有属性
		String fieldnames[] = getFieldsName(clazz);
		String sql1 = "select * from " + tablename;
		String sql2 = " where ";
		String sql = "";
		List<T> list = null;
		try {
			for (String s : fieldnames) {
				if (getMethods.get(s).invoke(obj) != null) {
					if (!getMethods.get(s).invoke(obj).toString().equals("0")) {
						sql2 += s + "='" + getMethods.get(s).invoke(obj) + "' or ";
					}
				}
			}
			sql = sql1 + sql2;
			if (sql.endsWith("where ")) {
				sql = sql.substring(0, sql.lastIndexOf("where "));
			} else {
				sql = sql1 + sql2.substring(0, sql2.lastIndexOf("or"));
			}
			System.out.println(sql);
			list = (List<T>) getForList(clazz, sql);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	// select count(*) from
	public <T>  Object selectcount(FanInterface<T> obj) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		String sql = "select count(*) from " + tablename;
		System.out.println(sql);
		return getForValue(sql);

	}
   //select sum(?) from
	public <T>  Object selectsum(FanInterface<T> obj,String fieldname) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		String sql = "select sum("+fieldname+") from " + tablename;
		System.out.println(sql);
		return getForValue(sql);

	}
	   //select avg(?) from
	public <T>  Object selectavg(FanInterface<T> obj,String fieldname) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		String sql = "select avg("+fieldname+") from " + tablename;
		System.out.println(sql);
		return getForValue(sql);

	}
	// selectlimitand
	@SuppressWarnings("unchecked")
	public <T> List<T> selectlimitand(FanInterface<T> obj, int begin, int end) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		// 获得属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz);
		// 获取所有属性
		String fieldnames[] = getFieldsName(clazz);
		String sql1 = "select * from " + tablename;
		String sql2 = " where ";
		String sql3 = "limit " + begin +","+ end;
		String sql = "";
		List<T> list = null;
		try {
			for (String s : fieldnames) {
				if (getMethods.get(s).invoke(obj) != null) {
					if (!getMethods.get(s).invoke(obj).toString().equals("0")) {
						sql2 += s + "='" + getMethods.get(s).invoke(obj) + "' and ";
					}
				}
			}
			sql = sql1 + sql2;
			if (sql.endsWith("where ")) {
				sql = sql.substring(0, sql.lastIndexOf("where ")) + sql3;
			} else {
				sql = sql1 + sql2.substring(0, sql2.lastIndexOf("and")) + sql3;
			}
			System.out.println(sql);
			list = (List<T>) getForList(clazz, sql);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;

	}
	
	//select from where like
	@SuppressWarnings("unchecked")
	public <T> List<T> selectlike(FanInterface<T> obj) {
		Class<?> clazz = obj.getClass();
		String tablename = clazz.getSimpleName().toLowerCase();
		// 获得属性名及get方法
		Map<String, Method> getMethods = getGetMethods(clazz);
		// 获取所有属性
		String fieldnames[] = getFieldsName(clazz);
		String sql1 = "select * from " + tablename;
		String sql2 = " where ";
		String sql = "";
		List<T> list = null;
		try {
			for (String s : fieldnames) {
				if (getMethods.get(s).invoke(obj) != null) {
					if (!getMethods.get(s).invoke(obj).toString().equals("0")) {
						sql2 += s + " like '%" + getMethods.get(s).invoke(obj) + "%' or ";
					}
				}
			}
			sql = sql1 + sql2;
			if (sql.endsWith("where ")) {
				sql = sql.substring(0, sql.lastIndexOf("where "));
			} else {
				sql = sql1 + sql2.substring(0, sql2.lastIndexOf("or"));
			}
			System.out.println(sql);
			list = (List<T>) getForList(clazz, sql);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	  //select order by desc
	  //String fieldsname 根据传人的fieldsname进行排序
	 @SuppressWarnings("unchecked")
	public <T> List<T> selectOBDesc(Object obj,String fieldsname){
		 Class<?> clazz = obj.getClass();
			String tablename = clazz.getSimpleName().toLowerCase();
			// 获得属性名及get方法
			Map<String, Method> getMethods = getGetMethods(clazz);
			// 获取所有属性
			String fieldnames[] = getFieldsName(clazz);
			String sql1 = "select * from " + tablename;
			String sql2 = " where ";
			String sql3 = "order by " + fieldsname +" desc ";
			String sql = "";
			List<T> list = null;
			try {
				for (String s : fieldnames) {
					if (getMethods.get(s).invoke(obj) != null) {
						if (!getMethods.get(s).invoke(obj).toString().equals("0")) {
							sql2 += s + "='" + getMethods.get(s).invoke(obj) + "' and ";
						}
					}
				}
				sql = sql1 + sql2;
				if (sql.endsWith("where ")) {
					sql = sql.substring(0, sql.lastIndexOf("where ")) + sql3;
				} else {
					sql = sql1 + sql2.substring(0, sql2.lastIndexOf("and")) + sql3;
				}
				System.out.println(sql);
				list = (List<T>) getForList(clazz, sql);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return list;
		 
	 }
	 
	//select groub by
    //String fieldsname 根据传人的fieldsname进行分组
	 @SuppressWarnings("unchecked")
	public <T> List<T> selectGB(Object obj,String fieldsname){
		 Class<?> clazz = obj.getClass();
			String tablename = clazz.getSimpleName().toLowerCase();
			// 获得属性名及get方法
			Map<String, Method> getMethods = getGetMethods(clazz);
			// 获取所有属性

			String fieldnames[] = getFieldsName(clazz);
			String sql1 = "select * from " + tablename;
			String sql2 = " where ";
			String sql3 = "group by " + fieldsname;
			String sql = "";
			List<T> list = null;
			try {
				for (String s : fieldnames) {
					if (getMethods.get(s).invoke(obj) != null) {
						if (!getMethods.get(s).invoke(obj).toString().equals("0")) {
							sql2 += s + "='" + getMethods.get(s).invoke(obj) + "' and ";
						}
					}
				}
				sql = sql1 + sql2;
				if (sql.endsWith("where ")) {
					sql = sql.substring(0, sql.lastIndexOf("where ")) + sql3;
				} else {
					sql = sql1 + sql2.substring(0, sql2.lastIndexOf("and")) + sql3;
				}
				System.out.println(sql);
				list = (List<T>) getForList(clazz, sql);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return list;
		 
	 }
	 
	// 执行sql(增，删，改)
	public void executeSql(String sql) {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = ConnectionManager.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
			System.out.println("执行成功");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.releaseDB(null, ps, conn);
		}

	}
	// 执行sql(查)

	public <T> List<T> getForList(Class<T> clazz, String sql) {

		List<T> list = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// 1. 得到结果集
			connection = ConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement(sql);

			resultSet = preparedStatement.executeQuery();

			// 2. 处理结果集, 得到 Map 的 List, 其中一个 Map 对象
			// 就是一条记录. Map 的 key 为 reusltSet 中列的别名, Map 的 value
			// 为列的值.
			List<Map<String, Object>> values = handleResultSetToMapList(resultSet);

			// 3. 把 Map 的 List 转为 clazz 对应的 List
			// 其中 Map 的 key 即为 clazz 对应的对象的 propertyName,
			// 而 Map 的 value 即为 clazz 对应的对象的 propertyValue
			list = transfterMapListToBeanList(clazz, values);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.releaseDB(resultSet, preparedStatement, connection);
		}

		return list;
	}


	// 返回某条记录的某一个字段的值 或 一个统计的值(一共有多少条记录等.)
	@SuppressWarnings("unchecked")
	public <E> E getForValue(String sql) {

		// 1. 得到结果集: 该结果集应该只有一行, 且只有一列
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// 1. 得到结果集
			connection = ConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return (E) resultSet.getObject(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionManager.releaseDB(resultSet, preparedStatement, connection);
		}
		// 2. 取得结果

		return null;
	}

	public <T> List<T> transfterMapListToBeanList(Class<T> clazz, List<Map<String, Object>> values)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException {

		List<T> result = new ArrayList<>();

		T bean = null;

		if (values.size() > 0) {
			for (Map<String, Object> m : values) {
				bean = clazz.newInstance();
				for (Map.Entry<String, Object> entry : m.entrySet()) {
					String propertyName = entry.getKey();
					// System.out.println(propertyName.toLowerCase());
					Object value = entry.getValue();

					// BeanUtils.setProperty(bean, propertyName, value);
					Field f = clazz.getDeclaredField(propertyName.toLowerCase());
					Object obj = gett(value);
					f.setAccessible(true);
					f.set(bean, obj);
				}
				// 把 Object 对象放入到 list 中.
				result.add(bean);
			}
		}

		return result;
	}

	/*
	 * 处理结果集, 得到 Map 的一个 List, 其中一个 Map 对象对应一条记录
	 */
	public List<Map<String, Object>> handleResultSetToMapList(ResultSet resultSet) throws SQLException {
		// 准备一个 List<Map<String, Object>>:
		// 键: 存放列的别名, 值: 存放列的值. 其中一个 Map 对象对应着一条记录
		List<Map<String, Object>> values = new ArrayList<>();

		List<String> columnLabels = getColumnLabels(resultSet);
		Map<String, Object> map = null;

		// 处理 ResultSet, 使用 while 循环
		while (resultSet.next()) {
			map = new HashMap<>();

			for (String columnLabel : columnLabels) {
				Object value = resultSet.getObject(columnLabel);
				map.put(columnLabel, value);
			}

			//  把一条记录的一个 Map 对象放入 5 准备的 List 中
			values.add(map);
		}
		return values;
	}

	/*
	 * 获取结果集的 ColumnLabel 对应的 List
	 */
	private List<String> getColumnLabels(ResultSet rs) throws SQLException {
		List<String> labels = new ArrayList<>();

		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			labels.add(rsmd.getColumnLabel(i + 1));
		}
		return labels;
	}

	public static Object gett(Object obj) {
		if (obj == null) {
			return 0;
		}
		return obj;

	}
}
