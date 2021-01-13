package com.zhihewulian.apphelper;

import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import dd.util.U;
import dd.util.UI;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    static {
        new Server().start(11111);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            U.init(this);
            U.delFile(Client.APK);
            WebView wv = findViewById(R.id.wv);
            UI.init(wv);
            wv.loadUrl("file:///android_asset/index.html");
            UI.CmdEvent = new UI.CmdEventListener() {
                @Override
                public String onEvent(JSONObject jo) throws Exception {
                    switch (jo.getString("c")) {
                        case "onload":
                            return U.getNetworkInfo().toString();
                    }
                    return null;
                }
            };
        } catch (Exception e) {
            U.log(e);
        }
    }
}