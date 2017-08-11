package com.ike.sqlitedb.sqllitedb.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
* author ike
* create time 22:39 2017/8/8
* function: 数据库操作工厂类
**/

public class BaseDaoFactory {
    private String Tag="BaseDaoFactory";
    //数据库表路径
    private String sqlLiteDataBasePath;
    private SQLiteDatabase mDataBase;
    private  static BaseDaoFactory instance=new BaseDaoFactory();
    private String dataBaseName;
    private BaseDaoFactory(){
        if (!TextUtils.isEmpty(dataBaseName)){
            sqlLiteDataBasePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+dataBaseName;
        }else {
            sqlLiteDataBasePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/default.db";
        }

        openDataBase();
    }
    public static BaseDaoFactory getInstance(){
        return instance;
    }

    /**
     * 设置数据库的名称
     * @param dataBaseName
     * @return
     */
    public BaseDaoFactory setDataBaseName(String dataBaseName){
        this.dataBaseName=dataBaseName;
        return this;
    }

    /**
     * 开启或者是创建数据库
     */
    private void openDataBase() {
        this.mDataBase=SQLiteDatabase.openOrCreateDatabase(sqlLiteDataBasePath,null);

    }

    /**
     *
     * 返回数据库dao操作类
     * @param daoClass 所要操作的dao 如userDao
     * @param entityClass 所要操作的数据库表对应的数据实体 如User
     * @param <T>
     * @param <M>
     * @return
     */
    public synchronized <T extends BaseDao<M>,M> T getDataBaseHelper(Class<T> daoClass,Class<M> entityClass){
        BaseDao baseDao=null;
        try {
            baseDao=daoClass.newInstance();
            baseDao.init(entityClass,mDataBase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

}
