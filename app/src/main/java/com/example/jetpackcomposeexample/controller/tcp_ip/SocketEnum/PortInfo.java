package com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum;

public enum PortInfo {
    MAIN_SUB1(1,false,51102),
    SUB1_MAIN(1,true,52102),
    MAIN_SUB2(2,false,51103),
    SUB2_MAIN(2,true,52103),
    MAIN_SUB3(3,false,51104),
    SUB3_MAIN(3,true,52104);
    public final int port;
    final public int key;
    final public boolean isMainCtrlPort;
    public static PortInfo fromPort(int v) {
        for (PortInfo x : values()) if (x.port == v) return x;
        return values()[0];
    }
    public static int findPort(int v, boolean isMainCtrlPort) {
        for (PortInfo x : values()) if ((x.key == v) && (isMainCtrlPort == x.isMainCtrlPort)) return x.port;
        return values()[0].port;
    }
    public static int findSameKey(int v) {
        for (PortInfo x : values()) {
            if ((x.key == fromPort(v).key) && (x.port!=v)) return x.port;
        }
        return values()[0].port;
    }
    public static boolean isMainControllerPort(int port) {
        for (PortInfo x: values()) {
            if((x.port == port) && (x.isMainCtrlPort)) return true;
        }
        return false;
    }

    PortInfo(int key, boolean isMainCtrlPort, int port) {
        this.key = key;
        this.isMainCtrlPort = isMainCtrlPort;
        this.port = port;
    }
}
