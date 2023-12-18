package com.ziji.imeitool;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Random;

/* loaded from: classes3.dex */
public class Util {
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
            sb.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNumber(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt("0123456789".length());
            sb.append("0123456789".charAt(number));
        }
        return sb.toString();
    }

    public static String getBeginStr() {
        Random random = new Random();
        if (random.nextInt() % 2 > 0) {
            return "35";
        }
        return "86";
    }

    public static String getIMEI1(Context context) {
        return getImeiOrMeid(context, 0);
    }

    public static String getIMEI2(Context context) {
        String imeiDefault = getIMEI1(context);
        if (TextUtils.isEmpty(imeiDefault)) {
            return "";
        }
        String imei1 = getImeiOrMeid(context, 0);
        String imei2 = getImeiOrMeid(context, 1);
        if (TextUtils.equals(imei2, imeiDefault)) {
            return !TextUtils.equals(imei1, imeiDefault) ? imei1 : "";
        }
        return imei2;
    }

    public static String getImeiOrMeid(Context context, int slotId) {
        String imei = "";
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            return "no permission";
        }
        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            if (manager != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    Method method = manager.getClass().getMethod("getImei", Integer.TYPE);
                    imei = (String) method.invoke(manager, Integer.valueOf(slotId));
                } else if (Build.VERSION.SDK_INT >= 21) {
                    imei = getSystemPropertyByReflect("ril.gsm.imei");
                    PrintStream printStream = System.err;
                    printStream.println("imei:" + imei);
                }
            }
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(imei)) {
            return getDeviceId(context, slotId);
        }
        return imei;
    }

    public static String getImeiOnly(Context context, int slotId) {
        String imei = "";
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            return "";
        }
        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            if (manager != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    Method method = manager.getClass().getMethod("getImei", Integer.TYPE);
                    imei = (String) method.invoke(manager, Integer.valueOf(slotId));
                } else if (Build.VERSION.SDK_INT >= 21) {
                    imei = getSystemPropertyByReflect("ril.gsm.imei");
                }
            }
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(imei)) {
            String imeiOrMeid = getDeviceId(context, slotId);
            if (!TextUtils.isEmpty(imeiOrMeid) && imeiOrMeid.length() >= 15) {
                return imeiOrMeid;
            }
            return imei;
        }
        return imei;
    }

    public static String getMeidOnly(Context context, int slotId) {
        String meid = "";
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            return "";
        }
        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            if (manager != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    Method method = manager.getClass().getMethod("getMeid", Integer.TYPE);
                    meid = (String) method.invoke(manager, Integer.valueOf(slotId));
                } else if (Build.VERSION.SDK_INT >= 21) {
                    meid = getSystemPropertyByReflect("ril.cdma.meid");
                }
            }
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(meid)) {
            String imeiOrMeid = getDeviceId(context, slotId);
            if (imeiOrMeid.length() == 14) {
                return imeiOrMeid;
            }
            return meid;
        }
        return meid;
    }

    public static String getSystemPropertyByReflect(String key) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method getMethod = clz.getMethod("get", String.class, String.class);
            PrintStream printStream = System.out;
            printStream.println(key + ";imei:" + ((String) getMethod.invoke(clz, key, "")));
            return (String) getMethod.invoke(clz, key, "");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDeviceId(Context context) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0 || !TextUtils.isEmpty("")) {
            return "";
        }
        String imei = getDeviceIdByReflect(context);
        return imei;
    }

    public static String getDeviceId(Context context, int slotId) {
        if (!TextUtils.isEmpty("")) {
            return "";
        }
        String imei = getDeviceIdByReflect(context, slotId);
        return imei;
    }

    public static String getDeviceIdFromSystemApi(Context context, int slotId) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            if (telephonyManager == null) {
                return "";
            }
            if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                return "TODO";
            }
            String imei = telephonyManager.getDeviceId(slotId);
            return imei;
        } catch (Throwable th) {
            return "";
        }
    }

    public static String getDeviceIdByReflect(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            if (Build.VERSION.SDK_INT >= 21) {
                Method simMethod = TelephonyManager.class.getDeclaredMethod("getDefaultSim", new Class[0]);
                Object sim = simMethod.invoke(tm, new Object[0]);
                Method method = TelephonyManager.class.getDeclaredMethod("getDeviceId", Integer.TYPE);
                return method.invoke(tm, sim).toString();
            }
            Class<?> clazz = Class.forName("com.android.internal.telephony.IPhoneSubInfo");
            Method subInfoMethod = TelephonyManager.class.getDeclaredMethod("getSubscriberInfo", new Class[0]);
            subInfoMethod.setAccessible(true);
            Object subInfo = subInfoMethod.invoke(tm, new Object[0]);
            Method method2 = clazz.getDeclaredMethod("getDeviceId", new Class[0]);
            return method2.invoke(subInfo, new Object[0]).toString();
        } catch (Throwable th) {
            return "";
        }
    }

    public static String getDeviceIdByReflect(Context context, int slotId) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            Method method = tm.getClass().getMethod("getDeviceId", Integer.TYPE);
            return method.invoke(tm, Integer.valueOf(slotId)).toString();
        } catch (Throwable th) {
            return "";
        }
    }

    public static String getImsiByReflect(Context context) {
        if (Build.VERSION.SDK_INT > 28) {
            return "";
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
            Method method = tm.getClass().getMethod("getSubscriberId", new Class[0]);
            return method.invoke(tm, new Object[0]).toString();
        } catch (Throwable th) {
            return "";
        }
    }
}
