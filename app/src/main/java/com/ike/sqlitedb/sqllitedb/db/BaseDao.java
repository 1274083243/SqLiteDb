package com.ike.sqlitedb.sqllitedb.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ike.sqlitedb.sqllitedb.db.anotation.DBField;
import com.ike.sqlitedb.sqllitedb.db.anotation.DBTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author ike
 * create time 22:36 2017/8/8
 * function: 数据库基本操作实现类
 **/

public abstract class BaseDao<T> {
    private String Tag = "BaseDao";
    protected SQLiteDatabase mSqliteDataBase;
    private boolean isFirstInit;
    private Class<T> entity;
    protected String tableName;
    private Map<Field, String> cacheMap = new HashMap<>();
    private List<String> whereClause = new ArrayList<>();
    private List<String> whereArgs = new ArrayList<>();
    private String orderBy;
    private String limit;

    /**
     * 初始化BaseDao
     *
     * @param entity         数据库表操作的数据实体
     * @param sqLiteDatabase
     * @return
     */
    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        this.entity = entity;
        try {
            if (isFirstInit == false) {
                this.mSqliteDataBase = sqLiteDatabase;
                //获取注解表名是否为空
                if (entity.getAnnotation(DBTable.class) == null) {
                    tableName = entity.getSimpleName();
                } else {
                    tableName = entity.getAnnotation(DBTable.class).values();
                }
                //判断数据库是否打开
                if (!mSqliteDataBase.isOpen()) {
                    return false;
                }
                String table = createTable(entity);
                if (!TextUtils.isEmpty(table)) {
                    mSqliteDataBase.execSQL(table);
                }
                initCacheMap();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        isFirstInit = true;
        return isFirstInit;


    }

    /**
     * 创建数据库表
     */
    private String createTable(Class<T> entity) {
        //"create table if not exists tb_user(username varchar(20),pwd varchar(10))";
        List<String> fieldNames = new ArrayList<>();
        String sql = "";
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + tableName + " (id integer primary key autoincrement,");
        Field[] fields = entity.getFields();
        for (Field field : fields) {
            String columnName = "";
            field.setAccessible(true);
            DBField dbField = field.getAnnotation(DBField.class);
            if (dbField != null) {
                columnName = dbField.values();
            } else {
                String name = field.getName();
                if ("$change".equals(name) || "serialVersionUID".equals(name)) {
                    continue;
                }
                columnName = field.getName();
            }
            if (!TextUtils.isEmpty(columnName)) {
                fieldNames.add(columnName);
            }
        }
        //拼接建表的sql语句
        if (fieldNames.size() > 0) {
            for (String columnName : fieldNames) {
                sb.append(columnName + " varchar(20),");
            }
            sql = sb.toString().substring(0, sb.toString().lastIndexOf(",")) + ")";
        }

        return sql;
    }


    /**
     * 将数据库的表的列名与相应的java对象的字段名相对应
     */
    private void initCacheMap() {
        String sql = "select * from " + this.tableName + " limit 1,0";
        Cursor cursor = null;
        cursor = mSqliteDataBase.rawQuery(sql, null);
        try {
            //获取数据库里面的字段名
            String[] columnNames = cursor.getColumnNames();
            //开始进行关系映射
            //获取java类中的字段名
            Field[] fields = entity.getDeclaredFields();
            for (Field field : fields) {
                //设置private类型的字段可以被反射使用
                field.setAccessible(true);
            }
            //进行关系映射:数据库字段名——》java类的字段名
            for (String columnName : columnNames) {
                Field columnFiled = null;
                String fieldName = null;
                for (Field field : fields) {
                    //判断字段上面是否拥有字段注解信息
                    DBField annotation = field.getAnnotation(DBField.class);
                    if (annotation != null) {
                        fieldName = annotation.values();
                    } else {
                        fieldName = field.getName();
                    }
                    if (columnName.equals(fieldName)) {
                        columnFiled = field;
                        break;
                    }
                }
                if (columnFiled != null) {
                    cacheMap.put(columnFiled, columnName);
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }


    }

    /**
     * 插入
     * @param entity
     * @return
     */
    public long insert(T entity) {
        Map<String, String> value = getValue(entity);
        ContentValues contentValues = getContentValues(value);
        long num = mSqliteDataBase.insert(tableName, null, contentValues);
        return num;
    }

    /**
     * 将java对象转化为map对象
     *
     * @param entity
     * @return
     */
    private Map<String, String> getValue(T entity) {
        Map<String, String> result = new HashMap<>();
        String cacheKey;
        String cacheValue;
        for (Map.Entry<Field, String> entry : cacheMap.entrySet()) {
            DBField annotation = entry.getKey().getAnnotation(DBField.class);
            if (annotation != null) {
                String values = annotation.values();
                cacheKey = values;
                try {
                    Object fieldValue = entry.getKey().get(entity);
                    if (fieldValue == null) {
                        continue;
                    }
                    cacheValue = (String) fieldValue;
                    result.put(cacheKey, cacheValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    @NonNull
    private ContentValues getContentValues(Map<String, String> result) {
        ContentValues contentValue = new ContentValues();
        if (result.size() > 0) {
            for (Map.Entry<String, String> entry : result.entrySet()) {
                contentValue.put(entry.getKey(), entry.getValue());
            }
        }
        return contentValue;
    }

    /**
     * 添加查询条件
     *
     * @param selection
     * @return
     */
    public BaseDao selection(String selection, String args) {
        whereClause.add(selection);
        whereArgs.add(args);
        return this;
    }

    /**
     * 排序规则
     * @param orderBy
     * @return
     */
    public BaseDao orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public BaseDao limit(String limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 查询
     * @return
     */
    public  List<T> query() {
        List<T> result = null;
        Cursor queryResult = null;
        try {

            result = null;
            if (whereArgs.size() > 0 && whereClause.size() > 0) {
                StringBuilder selectionBuilder = new StringBuilder();
                for (String clause : whereClause) {
                    selectionBuilder.append(clause + " and ");
                }
                String selection = selectionBuilder.toString().substring(0, selectionBuilder.toString().lastIndexOf("and"));
                String[] selectionArgs = whereArgs.toArray(new String[whereArgs.size()]);
                whereArgs.clear();
                whereClause.clear();
                String sql = "select * from " + tableName + " where " + selection;

                if (!TextUtils.isEmpty(orderBy)) {
                    sql = sql + " order by " + orderBy;
                }
                if (!TextUtils.isEmpty(limit)) {
                    sql = sql + " limit " + limit;
                }
                Log.e(Tag,"sql:"+sql);
                queryResult = mSqliteDataBase.rawQuery(sql, selectionArgs);
                if (queryResult != null && queryResult.getCount() > 0) {
                    result = getListFormCursor(queryResult, entity);
                }
            }
        } catch (Exception e) {

        } finally {
            if (queryResult != null) {
                queryResult.close();
            }

        }
        return result;
    }

    /**
     * 删除
     */
    public int delete() {
        String selection = null;
        String[] selectionArgs = null;
        if (whereArgs.size() > 0 && whereClause.size() > 0) {
            StringBuilder selectionBuilder = new StringBuilder();
            for (String clause : whereClause) {
                selectionBuilder.append(clause + " and ");
            }
            selection = selectionBuilder.toString().substring(0, selectionBuilder.toString().lastIndexOf("and"));
            selectionArgs = whereArgs.toArray(new String[whereArgs.size()]);
            whereArgs.clear();
            whereClause.clear();
        }
        int delete = mSqliteDataBase.delete(tableName, selection, selectionArgs);
        return delete;
    }

    /**
     * 数据跟新
     *
     * @param entity
     * @return
     */
    public int upDate(T entity) {
        String selection = null;
        String[] selectionArgs = null;
        if (whereArgs.size() > 0 && whereClause.size() > 0) {
            StringBuilder selectionBuilder = new StringBuilder();
            for (String clause : whereClause) {
                selectionBuilder.append(clause + " and ");
            }
            selection = selectionBuilder.toString().substring(0, selectionBuilder.toString().lastIndexOf("and"));
            selectionArgs = whereArgs.toArray(new String[whereArgs.size()]);
            whereArgs.clear();
            whereClause.clear();
        }
        int update = mSqliteDataBase.update(tableName, getContentValues(getValue(entity)), selection, selectionArgs);
        return update;
    }

    /**
     * 将cursor转化为list集合
     */
    private List<T> getListFormCursor(Cursor query, Class<T> entity) {
        List<T> result = new ArrayList<>();
        Object item;
        while (query.moveToNext()) {
            try {
                item = entity.newInstance();
                for (Map.Entry<Field, String> entry : cacheMap.entrySet()) {
                    String columnName = entry.getValue();
                    Field field = entry.getKey();
                    //获取列名的下标
                    int columnIndex = query.getColumnIndex(columnName);
                    //获取字段的类型
                    Class<?> type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            field.set(item, query.getString(columnIndex));
                        }
                        if (type == Integer.class) {
                            field.set(item, query.getInt(columnIndex));
                        }
                        if (type == double.class) {
                            field.set(item, query.getDouble(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                result.add((T) item);

            } catch (Exception e) {
            }
        }
        return result;
    }

}
