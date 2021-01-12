package dd.util;

import android.webkit.*;
import org.json.JSONObject;

public class UI {
    public static String HTML = U.cacheFile("ui.html");
    public static CmdEventListener CmdEvent;

    private static WebView wv;

    private UI() {
    }

    public static void init(WebView wv) {
        UI.wv = wv;
        wv.setWebContentsDebuggingEnabled(true);
        wv.setWebChromeClient(new WCC());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new JSInterface(), "jsi");
    }

    public static void callJS(final String data) {
        U.ACTIVITY.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wv.loadUrl("javascript:android_call(" + data + ")");
            }
        });
    }

    private static class WCC extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            U.log("webview exception " + cm.message());
            return super.onConsoleMessage(cm);
        }
    }

    private static class JSInterface {

        @JavascriptInterface
        public String cmd(String s) {
            try {
                String v = CmdEvent.onEvent(new JSONObject(s));
                if (v == null)
                    return "{\"r\":0}";
                else
                    return "{\"r\":0,\"v\":" + v + "}";
            } catch (Exception e) {
                return "{\"r\":1,\"v\":\"" + e + "\"}";
            }
        }
    }

    public interface CmdEventListener {
        String onEvent(JSONObject jo) throws Exception;
    }
}