package com.echo.p2p_project.server.model;

import com.echo.p2p_project.server.ServerMain;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;

import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-17:52
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.model
 * @Description:
 **/
public class WatchDog {
    private static Thread watch_thread;
    private static Boolean running = true;

    public static void startWatchDog() {
        watch_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    for (int i = 0; i < ServerMain.UHPT.size(); i++) {
                        UUID uuid = (UUID) ServerMain.UHPT.keySet().toArray()[i];
                        Peer p = ServerMain.UHPT.get(uuid);
//                        System.out.println(p);
                        if (p == null) {
                            System.out.println("REMOVED(peer is null): " + uuid);
                            ServerMain.UHPT.remove(uuid);
                            continue;
                        }
                        p.setMissedHartBeat(p.getMissedHartBeat() + 1);
                        if (p.getMissedHartBeat() > 5) {
                            System.out.println();
                            System.out.println("==================================== REMOVE =====================================");
                            System.out.println("PEER (peer MissedHartBeat > 5): " + uuid);
                            for (UUID guid : ServerMain.UHPT.get(uuid).possessing.keySet()) {
                                System.out.println("Resources: " + guid);
                                ServerMain.UHRT.remove(guid);
                            }
                            ServerMain.UHPT.remove(uuid);
                            System.out.println("=================================================================================");
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("WatchDog Interrupted");
                    }
                }
            }
        });
        watch_thread.start();
        System.out.println("WatchDog Started !");
    }

    public static void stopWatchDog() {
        running = false;
//        watch_thread.interrupt();
    }
}
