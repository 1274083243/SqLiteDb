package com.ike.sqlitedb.sqllitedb;


import android.util.Log;

import com.ike.sqlitedb.sqllitedb.db.BaseDao;
import java.util.List;

/**
 * Created by dell on 2017/8/9.
 */

public class UserDao extends BaseDao<User> {
    private String Tag="UserDao";
        public User searchById(){
            List<User> query = selection("id=?", "1").query();
            Log.e(Tag,"query:"+query.get(0).userName);
            return query.get(0);
        }
}
