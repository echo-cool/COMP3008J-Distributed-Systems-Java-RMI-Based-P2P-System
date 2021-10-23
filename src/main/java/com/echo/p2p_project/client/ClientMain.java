package com.echo.p2p_project.client;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import com.echo.p2p_project.Util;
import com.echo.p2p_project.client.interfaces.P2P_FileRegistry;
import com.echo.p2p_project.client.model.CHeartBeat;
import com.echo.p2p_project.client.model.P2PFileImpl;
import com.echo.p2p_project.server.interfaces.ConstructRegistry;
import com.echo.p2p_project.server.interfaces.HeartBeatRegistry;
import com.echo.p2p_project.server.interfaces.HelloRegistryFacade;
import com.echo.p2p_project.server.interfaces.SyncingRegistry;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/19-15:22
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.client
 * @Description:
 **/
public class ClientMain {
    private static final String MainServerIP = "127.0.0.1";
    public static Peer peer;
    public static HashMap<UUID, Resource> DHRT = new LinkedHashMap();
    public static Integer retry_times = 0;
    private static String name = "Peer";
    private static String IP = Util.getIP();
    private static Integer Port = 35000;
    private static Integer timeoutMillis = 2000;
    private static Registry registry;
    private static Registry file_service;
    private static ConstructRegistry constructRegistry;
    private static HeartBeatRegistry heartBeatRegistry;
    private static SyncingRegistry syncingRegistry;
    private static Boolean hasStarted = false;
    private static Scanner sc;
    private static Thread service;
    private static Integer download_retry_count = 0;


    public static void main(String[] args) throws ConnectException {
        service = new Thread(new Runnable() {
            @Override
            public void run() {
                recover();
            }
        });
        service.start();
        try {
            service.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sc = new Scanner(System.in);
        System.out.println("");
        System.out.print(">>> ");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            switch (line) {
                case "\n":
                    System.out.print(">>> ");
                    break;
                case "i":
                    System.out.println("GUID: " + peer.getGUID());
                    System.out.println("P2P_Port: " + peer.getP2P_port());
                    System.out.println("Possessing: " + peer.getPossessing());
                    System.out.println("Possessing Size: " + peer.getPossessing().size());
                    System.out.println("DHRT: " + DHRT);
                    break;
                case "r":
                    System.out.print("Register Resource Name: ");
                    String res_name = sc.nextLine();
                    reg_file(res_name);
                    break;
                case "l":
                    System.out.print("Look up Filename: ");
                    String file_name = sc.nextLine();
                    HashMap res = look_up_file(file_name);
                    System.out.println(res);
                    break;
                case "d":
                    System.out.print("Download Filename: ");
                    String file_name_to_download = sc.nextLine();
                    download(file_name_to_download);


            }
            System.out.print(">>> ");
        }
    }

    private static void download(String file_name_to_download) {
        HashMap resources = look_up_file(file_name_to_download);
        download(resources);
        download_retry_count = 0;
    }

    private static void download(String file_name_to_download, Integer retry_count) {
        if (download_retry_count <= 5) {
            download_retry_count+=1;
            System.out.println("Retrying " + download_retry_count);
            HashMap resources = look_up_file(file_name_to_download);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            download(resources);
        }else{
            System.out.println("Give up download.");
            return;
        }
    }


    public static void init_peer() {
        Boolean center_status = false;
        Boolean file_status = false;
        hasStarted = false;
        try {
            registry = LocateRegistry.getRegistry(MainServerIP, Util.RMI_PORT);
            center_status = lookup_registry() && register_peer() ? true : false;
        } catch (RemoteException e) {
//            e.printStackTrace();
        }
        if (peer == null) {
            System.out.println("Server Not Running.");
            exit();
        }

        CHeartBeat.StartHeart(heartBeatRegistry, peer.getGUID());

        //Init file services
        try {
            file_service = LocateRegistry.createRegistry(Port);
            P2P_FileRegistry p2PFileRegistry = new P2PFileImpl();
            file_service.rebind("p2PFileRegistry", p2PFileRegistry);
            System.out.println("========== Client-Side RMI Started ! ==========");
            System.out.println("Port: " + Port);
            System.out.println("UnicastServer: " + file_service.toString());
            System.out.println("Registered: " + Arrays.toString(file_service.list()));
            System.out.println("===============================================");
            //init local file dirs
            System.out.println("Download DIR: " + System.getProperty("user.dir") + "/download/");
            System.out.println("Resource DIR: " + System.getProperty("user.dir") + "/res/");
            FileUtil.mkdir(System.getProperty("user.dir") + "/download/");
            FileUtil.mkdir(System.getProperty("user.dir") + "/res/");
            file_status = true;
        } catch (RemoteException e) {
//            e.printStackTrace();
        }
        if (file_status && center_status)
            hasStarted = true;

    }

    private static Boolean register_peer() {
        System.out.println("+++++++ Register & Construct Peer +++++++");
        Port = NetUtil.getUsableLocalPort(35000, 45000);
        try {
            peer = constructRegistry.ConstructPeer(name, IP, Port);
            System.out.println("+++++++ Peer Construct Success !  +++++++");
            System.out.println("Peer GUID: " + peer.getGUID());
            System.out.println("Peer: " + peer);
            System.out.println("+++++++     Register Finished     +++++++");
            return true;
        } catch (RemoteException e) {
            hasStarted = false;
            System.out.println("register_peer RemoteException");
            e.printStackTrace();
            return false;
        }
    }

    private static Boolean lookup_registry() {
        try {
            HelloRegistryFacade hello = (HelloRegistryFacade) registry.lookup("HelloRegistry");
            String response = hello.helloWorld(name);
            System.out.println("=======> " + response + " <=======");
            if (response == null) {
                System.out.println("server failed");
                return false;
            }
            constructRegistry = (ConstructRegistry) registry.lookup("constructRegistry");
            heartBeatRegistry = (HeartBeatRegistry) registry.lookup("heatBeatRegistry");
            syncingRegistry = (SyncingRegistry) registry.lookup("syncingRegistry");
            return true;

        } catch (NotBoundException | RemoteException e) {
            System.out.println("lookup_registry RemoteException");
//            e.printStackTrace();
            return false;
        }

    }

    private static Boolean reg_file(String name) {
        File file = new File("res/" + name);
        if (!file.exists()) {
            System.out.println("File not exists !");
            System.out.println("This File is not exist in your file system !");
            return false;
        } else {
            System.out.println("File ok.");
            System.out.println("File length: " + file.length());
        }
        try {
            Resource resource = constructRegistry.ConstructResource(peer.getGUID(), name);
            ClientMain.DHRT.put(resource.getGUID(), resource);
            DHRT.put(resource.getGUID(), resource);
            System.out.println("File Register Success!");
            System.out.println("Resource GUID: " + resource.getGUID());
            sync_peer();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("File Register Error !");
            return false;
        }
        return true;
    }

    private static void sync_peer() {
        try {
            peer = syncingRegistry.syncPeer(peer.getGUID());
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Peer Sync Failed !");
        }
        System.out.println("Peer Sync Finished !");
    }

    private static HashMap<UUID, Resource> look_up_file(String file_name) {
        HashMap<UUID, Resource> result = new LinkedHashMap<>();
        System.out.println("Looking for: " + file_name);
        for (Resource r : DHRT.values()) {
            if (r.getName().equals(file_name)) {
                System.out.println("FOUND in local DHRT.");
                result.put(r.getGUID(), r);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DHRT = syncingRegistry.syncUHRT();
                            System.out.println(Thread.currentThread() + "sync UHRT Finished.");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        if (result.size() == 0)
            System.out.println("Not found in local DHRT.");
        else
            return result;
        try {
            DHRT = syncingRegistry.syncUHRT();
            System.out.println("DHRT Sync Finished !");
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("DHRT Sync Failed !");
        }
        for (Resource r : DHRT.values()) {
            if (r.getName().equals(file_name)) {
                System.out.println("FOUNT Resource in DHRT (After SYNC REMOTE UHRT)");
                result.put(r.getGUID(), r);
            }
        }
        if (result.size() == 0)
            System.out.println("Requested Resource NOT FOUND !");
        return result;
    }

    private static void download(HashMap<UUID, Resource> resources) {
        if (resources.size() <= 0) {
            System.out.println("Resource can not be download !");
            return;
        }
        if (resources.size() > 1) {
            System.out.println("There are more than 1 resource with identical name:");
            System.out.println("File List: ");
            for (Resource r : resources.values()) {
                System.out.println(r);
            }
            System.out.println("Please specify the GUID of the resource: ");
            UUID GUID = null;
            try {
                GUID = UUID.fromString(sc.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("UUID Error");
                return;
            }
            if (GUID == null) {
                System.out.println("UUID Error");
                return;
            }
            Resource resource = resources.get(GUID);
            if (resource == null) {
                System.out.println("Input GUID not in DHRT");
                return;
            }
            Peer p = (Peer) resource.possessedBy.values().stream().sorted().toArray()[0];
            P2P_download(p, resource);
        } else {
            Resource resource = (Resource) resources.values().toArray()[0];
            if (resource == null)
                return;
            Peer p = (Peer) resource.possessedBy.values().stream().sorted().toArray()[0];
            P2P_download(p, resource);
        }
    }

    private static void P2P_download(Peer p, Resource resource) {
        Integer port = p.getP2P_port();
        String IP = p.getIP();
        Registry p2pRegistry = null;
        File file = null;
        try {
            p2pRegistry = LocateRegistry.getRegistry(IP, port);
            System.out.println("Try to connect:   "+ p.getGUID());
            P2P_FileRegistry p2PFileRegistry = (P2P_FileRegistry) p2pRegistry.lookup("p2PFileRegistry");
            System.out.println("Connected.");
            System.out.println("Downloading FROM: " + p.getGUID());
            file = p2PFileRegistry.download(resource.getGUID());
        } catch (RemoteException e) {
            System.out.println("RemoteException");
            try {
                DHRT = syncingRegistry.syncUHRT();
                download(resource.getName(), download_retry_count);
                return;
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
//            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        if (file != null) {

            System.out.println("File downloaded !");
            System.out.println("File length : " + file.length());

            try {
                FileUtil.writeToStream(file, new FileOutputStream("download/" + peer.getGUID() + "_FROM_" + p.getGUID() + "_" + resource.getName()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("P2P_download File Error");
            return;
        }

    }
//
//    private static void P2P_download(Peer p, Resource resource, Integer retry_count) {
//        retry_count += 1;
//        Integer port = p.getP2P_port();
//        String IP = p.getIP();
//        Registry p2pRegistry = null;
//        File file = null;
//        try {
//            p2pRegistry = LocateRegistry.getRegistry(IP, port);
//            P2P_FileRegistry p2PFileRegistry = (P2P_FileRegistry) p2pRegistry.lookup("p2PFileRegistry");
//            file = p2PFileRegistry.download(resource.getGUID());
//        } catch (RemoteException e) {
//            System.out.println("Retry Failed !");
//            try {
//                DHRT = syncingRegistry.syncUHRT();
//                System.out.println("Retrying " + retry_count);
//                if (retry_count <= 5) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                    P2P_download(p, resource, retry_count);
//                }
//                return;
//            } catch (RemoteException ex) {
//                ex.printStackTrace();
//            }
////            e.printStackTrace();
//        } catch (NotBoundException e) {
//            e.printStackTrace();
//        }
//        if (file != null) {
//            System.out.println("File downloaded !");
//            System.out.println("File length : " + file.length());
//
//            try {
//                FileUtil.writeToStream(file, new FileOutputStream("download/" + peer.getGUID() + "_FROM_" + p.getGUID() + "_" + resource.getName()));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            System.out.println("P2P_download File Error");
//            return;
//        }
//
//    }

    public static void exit() {
        System.exit(0);
    }

    public static void recover() {
        init_peer();
    }

    public static void recover(int retry_count) {
        retry_count += 1;
        System.out.println("Try to recover......." + retry_count);
        if (retry_count > 10) {
            System.out.println();
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("==================  CLIENT  ====================");
            System.out.println("================== SHUTDOWN ====================");
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println();
            exit();
        }
        service.interrupt();
        service = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recover();
            }
        });
        service.start();
    }
}
