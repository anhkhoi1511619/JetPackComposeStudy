package com.example.jetpackcomposeexample.controller.tcp_ip.connect;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PingChecker {
    public PingChecker(List<ClientController> clientControllerList) {
        this.clientControllerList = clientControllerList;
    }
    List<ClientController> clientControllerList;
    final ScheduledExecutorService pingExecutor = Executors.newSingleThreadScheduledExecutor();
    Map<ClientController, Boolean> pingMap = new HashMap<>();

    public boolean isReachable(ClientController client) {
        if(pingMap.isEmpty() || pingMap.get(client) == null) return true;
        return Boolean.TRUE.equals(pingMap.get(client));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void check() {
        Runnable task = () -> {
            try{
                for (ClientController client : clientControllerList) {
                    boolean reachable = InetAddress.getByName(client.getIP()).isReachable(10);
                    Log.d("PING_CHECK", "Is host reachable with IP "+client.getIP()+" " + reachable);
                    setPingStatus(client, reachable);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        };
        // 初期遅延時間: 1秒, 実行間隔: 2秒
        pingExecutor.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void setPingStatus(ClientController client, boolean reachable) {
        if(pingMap.isEmpty()) pingMap = new HashMap<>();
        if(pingMap.containsKey(client)){
            pingMap.replace(client, reachable);
        } else {
            pingMap.put(client, reachable);
        }
        pingMap.keySet().removeIf(Objects::isNull);
    }
}
