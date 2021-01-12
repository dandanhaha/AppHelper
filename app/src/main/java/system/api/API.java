package system.api;

import dd.util.U;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class API {

    public boolean exec(JSONObject jo) throws Exception {
        String c = jo.getString("v0");
        switch (c) {
            case "reboot":
                return reboot();
            case "lcdOn":
                return lcdOn();
            case "lcdOff":
                return lcdOff();
            case "lcdTime":
                return setLcdTime(jo.getInt("v1"));
            case "nbv":
                return setNavigationBarVisibility(jo.getBoolean("v1"));
            case "sbd":
                return setStatusBarDisplay(jo.getBoolean("v1"));
            case "network":
                JSONArray ja = jo.getJSONArray("v1");
                setNetwork(ja.getString(0), ja.getString(1), ja.getString(2), ja.getString(3));
                U.restartActivity();
                return true;
            default:
                throw new Exception("unsupport cmd " + c);
        }
    }

    public abstract boolean reboot();

    public abstract boolean lcdOn();

    public abstract boolean lcdOff();

    public abstract boolean setLcdTime(int seconds);

    public abstract boolean setNavigationBarVisibility(boolean v);

    public abstract boolean setStatusBarDisplay(boolean v);

    public abstract boolean setNetwork(String ip, String mask, String gateway, String dns);

    public abstract boolean install(String apkpath);
}