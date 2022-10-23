package cn.ecjtu.clock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;

public class MainActivity extends AppCompatActivity {
    private ComponentName componentName;

    private void activeManager() {
        //使用隐式意图调用系统方法来激活指定的设备管理器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DevicePolicyManager dpm = (DevicePolicyManager)
                        getSystemService(Context.DEVICE_POLICY_SERVICE);
                componentName = new ComponentName(getApplicationContext(),
                        AdminReceiver.class);
                if (dpm.isAdminActive(componentName)) {//判断是否有权限(激活了设备管理器)
                    dpm.lockNow();// 直接锁屏
                    //杀死当前应用
                    Process.killProcess(Process.myPid());
                } else {
                    activeManager();//激活设备管理器获取权限
                }
            }
        }).start();
    }
}