package com.zhihewulian.apphelper;

import dd.util.U;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<Client> list = new ArrayList<>();
    private long HeartOutTime = 30 * 1000;
    private ServerSocket svr;

    public void start(int port) {
        try {
            svr = new ServerSocket(port);
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket client = svr.accept();
                            Client c = new Client(client);
                            synchronized (list) {
                                list.add(c);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            long ts = System.currentTimeMillis();
                            synchronized (list) {
                                for (int i = 0; i < list.size(); i++) {
                                    Client c = list.get(i);
                                    if (ts - c.TS > HeartOutTime) {
                                        list.remove(c);
                                        c.Dispose();
                                        i--;
                                    }
                                }
                            }
                            Thread.currentThread().join(10000);
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            U.log(e);
        }
    }
}
