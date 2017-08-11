package com.ike.sqlitedb.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限帮助类
 */

public class PermissionHelper {
    private static Object  mObject;
    private static String Tag="PermissionHelper";
    private int mRequestCode;
    private static String[] mRequestPermissions;
    private static List<String> deniedPermissions=new ArrayList<>();
    private static List<String> alwaysDenyPermission=new ArrayList<>();
    private PermissionHelper(Object object){
        this.mObject= object;
    }


    /**
     * 添加权限请求码
     * @param requestCode
     * @return
     */
    public PermissionHelper addRequestCode(int requestCode){
        this.mRequestCode=requestCode;
        return this;
    }

    /**
     * 添加权限请求列表
     * @param requestPermissions
     * @return
     */
    public PermissionHelper addRequestPermissions(String... requestPermissions){
        this.mRequestPermissions=requestPermissions;
        return this;
    }

    /**
     * 添加activity上下文
     * @param activity
     * @return
     */
    public static PermissionHelper with(Activity activity){
        return new PermissionHelper(activity);
    }

    /**
     * 添加Fragment上下文
     * @param fragment
     * @return
     */
    public static PermissionHelper with(Fragment fragment){
        return new PermissionHelper(fragment);
    }
    public void requestPermissions(Activity activity,int requestCode,String... requestPermissions){
        PermissionHelper.with(activity).addRequestCode(requestCode).addRequestPermissions(requestPermissions);
    }
    public void requestPermissions(Fragment fragment,int requestCode,String... requestPermissions){
        PermissionHelper.with(fragment).addRequestCode(requestCode).addRequestPermissions(requestPermissions);
    }

    /**
     * 发起权限请求
     */
    public void request(){
        //判断当前版本是不是6.0以上
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            //大于等于6.0版本先请求相关权限，再执行相关方法
            getDeniedPermissionsList();
            if (deniedPermissions.size()==0){
                //权限已经全部授予，直接执行方法
                executeRequestPermissionSuccessMethod(mObject,mRequestCode);
            }else {
                //继续申请相关的权限
                ActivityCompat.requestPermissions(
                        getContext(),
                        deniedPermissions.toArray(new String[deniedPermissions.size()]),
                        mRequestCode);
            }
        }else {
            //小于6.0版本，反射执行相关方法
            executeRequestPermissionSuccessMethod(mObject,mRequestCode);
        }
    }

    /**
     * 反射执行权限申请成功的方法
     */
    private static void executeRequestPermissionSuccessMethod(Object object,int requestCode){
        //获取clazz中所有的方法，
        Method[] methods = object.getClass().getDeclaredMethods();
        //遍历所有方法，找到带有permissionSucess标记的方法，并且requestcode也要一致
        for (Method method:methods){
            //获取方法头上的注解信息
            PermissionSuccess success = method.getAnnotation(PermissionSuccess.class);
            if (success!=null){
                //获取注解内的参数信息
                int code = success.requestCode();
                //判断是否是我们想要查找的方法
                if (code==requestCode){
                    //反射执行该方法
                    try {
                        //允许执行私有方法
                        method.setAccessible(true);
                        method.invoke(object,new Object[]{});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
    /**
     * 反射执行权限申请失败的方法
     */
    private static void executeRequestPermissionFailMethod(Object object,int requestCode){
        //获取clazz中所有的方法，
        Method[] methods = object.getClass().getDeclaredMethods();
        //遍历所有方法，找到带有permissionSucess标记的方法，并且requestcode也要一致
        for (Method method:methods){
            //获取方法头上的注解信息
            PermissionFailed success = method.getAnnotation(PermissionFailed.class);
            if (success!=null){
                //获取注解内的参数信息
                int code = success.requestCode();
                //判断是否是我们想要查找的方法
                if (code==requestCode){
                    //反射执行该方法
                    try {
                        //允许执行私有方法
                        method.setAccessible(true);
                        method.invoke(object,new Object[]{});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
    /**
     * 反射执行权限永远不再提醒申请失败的方法
     */
    private static void executeRequestPermissionAlwaysFailMethod(Object object,int requestCode){
        //获取clazz中所有的方法，
        Method[] methods = object.getClass().getDeclaredMethods();
        //遍历所有方法，找到带有permissionSucess标记的方法，并且requestcode也要一致
        for (Method method:methods){
            //获取方法头上的注解信息
            PermissionNever success = method.getAnnotation(PermissionNever.class);
            if (success!=null){
                //获取注解内的参数信息
                int code = success.requestCode();
                //判断是否是我们想要查找的方法
                if (code==requestCode){
                    //反射执行该方法
                    try {
                        //允许执行私有方法
                        method.setAccessible(true);
                        method.invoke(object,new Object[]{});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    /**
     * 获取被拒绝的权限列表
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void getDeniedPermissionsList(){
        deniedPermissions.clear();
        alwaysDenyPermission.clear();
        for (String permission :mRequestPermissions){
            if (ContextCompat.checkSelfPermission(getContext(),permission)== PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }
            if (!getContext().shouldShowRequestPermissionRationale(permission)){
                alwaysDenyPermission.add(permission);
            }
        }

    }

    /**
     * 获取上下文
     */
    private static Activity getContext() {

            if (mObject instanceof Activity){
                return (Activity)mObject;
            }
            if (mObject instanceof Fragment){
                return ((Fragment)mObject).getActivity();
            }

        return null;


    }

    /**
     * 处理权限申请的回调结果
     * @param requestCode
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void handleRequestPermissionsResult(int requestCode) {
        if (deniedPermissions.size()==0){
            executeRequestPermissionSuccessMethod(mObject,requestCode);
        }else {
            if (alwaysDenyPermission.size()!=0){
                executeRequestPermissionAlwaysFailMethod(mObject,requestCode);
            }else {
                executeRequestPermissionFailMethod(mObject,requestCode);
            }


        }

    }
    /**
     * 获取被拒绝的权限列表集合
     * @return
     */
    public static List<String> getDenyPermissions(){
        return deniedPermissions;
    }

    /**
     * 获取永远被拒绝的权限集合
     * @return
     */
    public static List<String> getAlwayDenyPermissions(){

        return alwaysDenyPermission;
    }
}
