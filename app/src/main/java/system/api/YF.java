package system.api;

import android.content.Context;
import com.example.yf_a64_api.YF_A64_API_Manager;

public class YF extends API {
    private YF_A64_API_Manager yf;

    public YF(Context context) {
        yf = new YF_A64_API_Manager(context);
    }

    @Override
    public boolean reboot() {
        yf.yfReboot();
        return true;
    }

    @Override
    public boolean lcdOn() {
        yf.yfSetLCDOn();
        return true;
    }

    @Override
    public boolean lcdOff() {
        yf.yfSetLCDOff();
        return true;
    }

    @Override
    public boolean setLcdTime(int seconds) {
        return false;
    }

    @Override
    public boolean setNavigationBarVisibility(boolean v) {
        yf.yfsetNavigationBarVisibility(v);
        return true;
    }

    @Override
    public boolean setStatusBarDisplay(boolean v) {
        yf.yfsetStatusBarDisplay(v);
        return true;
    }

    @Override
    public boolean setNetwork(String ip, String mask, String gateway, String dns) {
        return yf.yfsetEthIPAddress(ip, mask, gateway, dns);
    }

    @Override
    public boolean install(String apkpath) {
        return yf.adbcommand("pm install " + apkpath);
    }
}
