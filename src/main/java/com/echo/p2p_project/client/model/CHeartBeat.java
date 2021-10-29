package com.echo.p2p_project.client.model;

import com.echo.p2p_project.client.ClientMain;
import com.echo.p2p_project.server.interfaces.HeartBeatRegistry;

import java.rmi.RemoteException;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-18:13
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.client.model
 * @Description:
 **/
public class CHeartBeat {
    public static Thread heart;
    private static Boolean running = true;


    public static void StartHeart(HeartBeatRegistry heartBeatRegistry, UUID GUID) {
        running = true;
        heart = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Start Heart Beat");
                while (running) {
                    try {
                        long start = System.currentTimeMillis();
                        Boolean status = heartBeatRegistry.heartBeat(GUID);
                        long end = System.currentTimeMillis();
//                        System.out.println( this.toString() + ": " + (end - start));
//                        System.out.println(status);
                        ClientMain.retry_times = 0;
                    } catch (RemoteException e) {
                        running = false;
                        System.out.println("Main Server DOWN !");
                        ClientMain.retry_times += 1;
                        ClientMain.recover(ClientMain.retry_times);
//                        ClientMain.exit();
//                        System.out.println("Trying to recover...");
//                        downCount+=1;
//                        System.out.println("DOWN COUNT: " + downCount);
//                        if(downCount > 5) {
//                            System.out.println("DOWN COUNT > 5: Exiting..");
//
//                        }

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        heart.start();
    }

    public static void endHeart() {
        running = false;
    }
}
