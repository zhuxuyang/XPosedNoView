package com.zxy.xposednoview;

import android.hardware.Sensor;
import android.util.SparseArray;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by zxy on 2017/3/1 0001.
 */

public class Test implements IXposedHookLoadPackage {
    private static int stepCount = 1;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // filter
        if (!(loadPackageParam.packageName.equals("com.tencent.mm")||loadPackageParam.packageName.equals("com.eg.android.AlipayGphone"))) {
            return;
        }

        final Class<?> sensorEL = findClass("android.hardware.SystemSensorManager$SensorEventQueue",loadPackageParam.classLoader);
        XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                ((float[]) param.args[1])[0] = ((float[]) param.args[1])[0] + 2000 * stepCount;
                stepCount++;

                Field field = param.thisObject.getClass().getEnclosingClass().getDeclaredField("sHandleToSensor");
                field.setAccessible(true);

                int handle = (Integer) param.args[0];
                Sensor sensor = ((SparseArray<Sensor>) field.get(0)).get(handle);
                XposedBridge.log("sensor = " + sensor);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

    }
}


