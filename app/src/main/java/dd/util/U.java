package dd.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.*;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class U {

    public static AppCompatActivity ACTIVITY;

    private U() {
    }

    public static void init(Context context) {
        ACTIVITY = (AppCompatActivity) context;
    }

    public static void log(Object obj) {
        Log.d("Log", obj + "");
        if (obj instanceof Exception) {
            Log.d("Log", ((Exception) obj).getStackTrace()[0] + "");
        }
    }

    public static String cacheFile(String name) {
        try {
            return ACTIVITY.getCacheDir().getAbsolutePath() + "/" + name;
        } catch (Exception e) {
            return null;
        }
    }

    public static void restartActivity() {
        try {
            ACTIVITY.finish();
            ACTIVITY.startActivity(ACTIVITY.getIntent());
        } catch (Exception e) {
        }
    }

    public static void closeActivity() {
        ACTIVITY.finish();
    }

    public static String encrypt(String data, String KEY, String IV) {
        try {
            DESKeySpec des = new DESKeySpec(KEY.getBytes("ASCII"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(des);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes("ASCII"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes("GB2312"));
            return new String(Base64.encode(bytes, Base64.NO_WRAP));
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static NetworkInfo getNetworkInfo() {
        try {
            NetworkInfo networkInfo = new NetworkInfo();

            ConnectivityManager cm = (ConnectivityManager) ACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo ni = cm.getActiveNetworkInfo();
            Network network = cm.getActiveNetwork();
            if (ni != null && ni.isAvailable()) {
                networkInfo.Type = ni.getTypeName();
                networkInfo.Mac = ni.getExtraInfo();
                LinkProperties lp = cm.getLinkProperties(network);
                for (LinkAddress la : lp.getLinkAddresses()) {
                    InetAddress addr = la.getAddress();
                    if (addr instanceof Inet4Address) {
                        networkInfo.IP = addr.getHostAddress();
                        break;
                    }
                }
                for (InetAddress addr : lp.getDnsServers()) {
                    networkInfo.DNS = addr.getHostAddress();
                    break;
                }
                List<RouteInfo> ris = lp.getRoutes();
                networkInfo.Mask = calcMask(ris.get(0).getDestination().getPrefixLength());
                networkInfo.Gateway = ris.get(1).getGateway().getHostAddress();
            }
            return networkInfo;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static String calcMask(int len) {
        long v = 0;
        for (int i = 0; i < len; i++) {
            v |= 1 << (31 - i);
        }
        return int2IP(v);
    }

    public static String int2IP(long v) {
        String s = "";
        for (int i = 0; i < 4; i++) {
            if (s != "")
                s += ".";
            s += v >> (3 - i) * 8 & 0xFF;
        }
        return s;
    }

}