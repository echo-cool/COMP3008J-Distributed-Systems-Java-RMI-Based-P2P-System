package com.echo.p2p_project.server;

import com.echo.p2p_project.server.interfaces.ConstructRegistry;
import com.echo.p2p_project.server.interfaces.HeartBeatRegistry;
import com.echo.p2p_project.server.interfaces.HelloRegistryFacade;
import com.echo.p2p_project.server.interfaces.FileLookupRegistry;
import com.echo.p2p_project.server.model.*;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;
import com.sun.javafx.collections.ObservableMapWrapper;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/19-15:22
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server
 * @Description:
 **/
public class ServerMain{
    public static final int RMI_PORT = 1099;
    public static ObservableMapWrapper<UUID, Peer> UHPT = new ObservableMapWrapper<>(new LinkedHashMap<>());
    public static ObservableMapWrapper<String, Resource> UHRT = new ObservableMapWrapper<>(new LinkedHashMap<>());
    private static Registry registry;
    private static Boolean HasStarted = false;
    private static Thread service;

    public static void main(String[] args) {
        service = new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        service.start();
        Scanner sc = new Scanner(System.in);
        System.out.println("");
        System.out.print(">>> ");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            switch (line) {
                case "\n":
                    System.out.print(">>> ");
                    break;
                case "i":
                    System.out.println("UHPT: " + UHPT);
                    System.out.println("UHPT Size: " + UHPT.size());
                    System.out.println("UHRT: " + UHRT);
                    System.out.println("UHRT Size: " + UHRT.size());
                    System.out.println("registry: " + registry);
                    break;
            }
            System.out.print(">>> ");
        }

    }

    public static void init() {
        try {
            // Start Registry, Port: 1099
            registry = LocateRegistry.createRegistry(RMI_PORT);
            reg_services();


            System.out.println("======= RMI Start Up! ============");
            System.out.println(registry.toString());
            System.out.println("Registered: " + Arrays.toString(registry.list()));
            for (String s : registry.list()) {
                System.out.println("Registered: " + s);
            }
            System.out.println("============ WatchDog ============");
            if (!HasStarted)
                WatchDog.startWatchDog();
            System.out.println("======= Start Up Finished ========");
            HasStarted = true;

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void reg_services() {
        try {
            HelloRegistryFacade hello = new HelloRegistryFacadeImpl();
            ConstructRegistry constructRegistry = new ConstructImpl();
            HeartBeatRegistry heartBeatRegistry = new HeartBeatImpl();
            FileLookupRegistry fileLookupRegistry = new FileLookupImpl();

            registry.rebind("HelloRegistry", hello);
            registry.rebind("constructRegistry", constructRegistry);
            registry.rebind("heatBeatRegistry", heartBeatRegistry);
            registry.rebind("syncingRegistry", fileLookupRegistry);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void exit(){
        service.interrupt();
    }
}
