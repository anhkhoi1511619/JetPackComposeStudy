package com.example.jetpackcomposeexample.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Shell {
    public static String run(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader((p.getInputStream())));
            String line = "";
            while ((line = reader.readLine()) !=null) {
                output.append(line).append("\n");
            }
            p.waitFor();
        } catch (Exception e) {
            output.append(e.getMessage()).append("\n");
        }
        return output.toString();
    }

    void test() {
        String IP = "192.168.254.30";
        String gateWay = "192.168.254.1";
        String mask = "192.168.254.0/24";

        Shell.run("setprop persist.vendor.ethname etho");
        Shell.run("setprop persist.vendor.ipaddress "+IP);
        Shell.run("setprop persist.vendor.gateway "+gateWay);
        Shell.run("setprop persist.vendor.mask "+mask);
        Shell.run("setprop persist.vendor.dns "+gateWay);
        Shell.run("setprop persist.vendor.ethernet-setting 1");
    }
}
