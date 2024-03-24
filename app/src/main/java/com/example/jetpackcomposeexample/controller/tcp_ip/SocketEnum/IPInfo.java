package com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum;

import androidx.annotation.NonNull;

public enum IPInfo {
    MAIN(1, "192.168.125.253"),
    SUB1(2, "192.168.125.82"),
    SUB2(3, "192.168.125.47"),
    SUB3(4, "192.168.254.33"),;
    final public int key;
    final public String IP;


    IPInfo(int key, String ip) {
        this.key = key;
        IP = ip;
    }
    public static IPInfo fromIP(String v) {
        for (IPInfo x : values()) if (x.IP.equals(v)) return x;
        return values()[0];
    }
    public static String fromKey(int v) {
        for (IPInfo x : values()) if (x.key == v) return x.IP;
        return values()[0].IP;
    }
    public static String findName(int v) {
        for (IPInfo x : values()) if (x.key == v) return x.name();
        return values()[0].name();
    }

    @NonNull
    public String toString() {
        switch (key) {
            case 1:
                return "メイン制御器";
            case 2:
                return "サブ制御器1";
            case 3:
                return "サブ制御器2";
            case 4:
                return "サブ制御器3";
        }
        return "";
    }
}
