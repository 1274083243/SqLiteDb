package com.ike.sqlitedb.sqllitedb;


import com.ike.sqlitedb.sqllitedb.db.anotation.DBField;
import com.ike.sqlitedb.sqllitedb.db.anotation.DBTable;

/**
 * Created by dell on 2017/8/8.
 */
@DBTable(values = "tb_user")
public class User {
    @DBField(values = "username")
    public String userName;
    @DBField(values = "pwd")
    public String pwd;
}
