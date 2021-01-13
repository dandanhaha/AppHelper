package com.zhihewulian.apphelper;

import java.io.*;
import java.net.Socket;

import android.os.Looper;
import dd.util.U;
import org.json.JSONObject;
import system.api.API;
import system.api.APIFactory;

import java.util.Date;

public class Client {
    public static String APK = U.cacheFile("install.apk");

    private static int SIZE = 1024 * 1024 + 6;
    private static byte[] HEADER = {(byte) 'N', (byte) 'C'};

    public long TS;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] buffer = new byte[SIZE];
    private int offset = 0;
    private int fileLength = 0;
    private boolean handshake = false;
    private String des;
    private FileOutputStream fs;
    private API api;

    public Client(Socket socket) {
        try {
            this.TS = System.currentTimeMillis();
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            run();
            String s = new Date().toString();
            des = U.encrypt(s, "beijingz", "hihewuli");
            this.Send(s);
        } catch (Exception e) {
        }
    }

    private void run() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {
                    try {
                        int n = inputStream.read(buffer, offset, SIZE - offset);
                        if (n == -1)
                            throw new Exception("Socket Close");
                        offset += n;
                        if (handshake)
                            TS = System.currentTimeMillis();
                        while (offset >= 6) {
                            if (buffer[0] == HEADER[0] && buffer[1] == HEADER[1]) {
                                int len = 0;
                                for (int i = 0; i < 4; i++)
                                    len += (buffer[2 + i] & 0xFF) << (i * 8);
                                int length = 6 + len;
                                if (len > SIZE - 6) {
                                    throw new Exception("Out of Buffer Size");
                                } else if (len == 0) {
                                    if (length < offset)
                                        System.arraycopy(buffer, length, buffer, 0, offset - length);
                                    offset -= length;
                                } else if (length <= offset) {
                                    byte[] data = new byte[len];
                                    System.arraycopy(buffer, 6, data, 0, len);
                                    if (length < offset)
                                        System.arraycopy(buffer, length, buffer, 0, offset - length);
                                    offset -= length;
                                    if (handshake) {
                                        if (fs != null) {
                                            fs.write(data);
                                            fileLength -= len;
                                            Send("{\"c\":\"apk\",\"v\":" + len + "}");
                                            if (fileLength == 0) {
                                                fs.close();
                                                fs = null;
                                                api.install(APK);
                                            }
                                        } else {
                                            String s = new String(data, "GB2312");
                                            try {
                                                Cmd(new JSONObject(s));
                                            } catch (Exception e) {
                                                U.log(e);
                                            }
                                        }
                                    } else {
                                        String s = new String(data, "GB2312");
                                        if (des.equals(s)) {
                                            handshake = true;
                                        } else {
                                            throw new Exception("Illegal Connection");
                                        }
                                    }
                                } else
                                    break;
                            } else {
                                throw new Exception("Header Error");
                            }
                        }
                    } catch (Exception e) {
                        Dispose();
                        return;
                    }
                }
            }
        }.start();
    }

    private void Cmd(JSONObject jo) throws Exception {
        U.log(jo);
        switch (jo.getString("c")) {
            case "type"://{"c":"type","v":"$typename"}
                api = APIFactory.create(jo.getString("v"));
                break;
            case "api"://{"c":"api","v0":"$apiname","v1":$args}
                boolean r = api.exec(jo);
                Send(jo.getString("v0"), r ? "\"success\"" : "\"fail\"");
                break;
            case "apk"://{"c":"apk","v":$filelength}
                fileLength = jo.getInt("v");
                fs = new FileOutputStream(APK);
                break;
            case "restart"://{"c":"restart"}
                U.restartActivity();
                break;
            case "close"://{"c":"close"}
                U.closeActivity();
                break;
            case "start"://{"c":"start","v":"$packageName"}
                String n = jo.getString("v");
                if (n.equals(""))
                    n = "com.zhihewulian.gateway";
                U.startActivity(n);
                break;
        }
    }

    private void Send(String c, Object v) {
        Send("{\"c\":\"" + c + "\",\"v\":" + v + "}");
    }

    private void Send(String s) {
        try {
            byte[] data = s.getBytes("GB2312");
            int len = data.length;
            byte[] buffer = new byte[6 + len];
            buffer[0] = HEADER[0];
            buffer[1] = HEADER[1];
            for (int i = 0; i < 4; i++)
                buffer[2 + i] = (byte) (len >> i * 8);
            System.arraycopy(data, 0, buffer, 6, len);
            this.outputStream.write(buffer);
            this.outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Dispose() {
        try {
            this.socket.close();
        } catch (Exception e) {
        }
    }
}