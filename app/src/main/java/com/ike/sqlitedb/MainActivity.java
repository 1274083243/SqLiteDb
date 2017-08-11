package com.ike.sqlitedb;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ike.sqlitedb.permission.PermissionFailed;
import com.ike.sqlitedb.permission.PermissionHelper;
import com.ike.sqlitedb.permission.PermissionNever;
import com.ike.sqlitedb.permission.PermissionSuccess;
import com.ike.sqlitedb.sqllitedb.User;
import com.ike.sqlitedb.sqllitedb.UserDao;
import com.ike.sqlitedb.sqllitedb.db.BaseDaoFactory;

import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE=101;
    private String Tag="MainActivity";
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionHelper
                .with(this)
                .addRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .addRequestCode(REQUEST_CODE)
                .request();
    }
    @PermissionSuccess(requestCode =REQUEST_CODE)
    private void onPermisstionGet(){
        userDao = BaseDaoFactory.getInstance().getDataBaseHelper(UserDao.class, User.class);
//        User user=new User();
//        user.pwd="123";
//        user.userName="ike";
//        userDao.insert(user);
    }
    @PermissionFailed(requestCode =REQUEST_CODE)
    private void onPermisstionFail(){
        Log.e(Tag,"拒绝了sd卡读写权限");
    }
    @PermissionNever(requestCode = REQUEST_CODE)
    private void onPermisstionAlwaysFail(){
        Log.e(Tag,"永远拒绝了sd卡读写权限");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.handleRequestPermissionsResult(requestCode);
    }
    @SuppressWarnings("unchecked")
    public void query(View view){

//        Log.e(Tag,"点解了");
        //List<User> list = userDao.selection("username=?", "ike123").query();
       // int delete = userDao.selection("username=?", "ike").delete();
//        User user=new User();
//        user.userName="ike123";
//        int upDate = userDao.selection("username=?", "ike").upDate(user);
userDao.searchById();
      //  Log.e(Tag,"listsize:"+list.get(0).pwd);
    }



}
