package com.echo.p2p_project.client;

import java.rmi.ConnectException;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/27-22:48
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.client
 * @Description:
 **/
public class Test {
    public static void main(String[] args) {
        //Limit testing
        for(int i = 0; i < 5000; i++){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ClientMain.recover();
                }
            }).start();


        }

    }
}
