package com.echo.p2p_project.client;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import com.echo.p2p_project.Util;
import com.echo.p2p_project.client.interfaces.P2P_FileRegistry;
import com.echo.p2p_project.client.model.CHeartBeat;
import com.echo.p2p_project.client.model.P2PFileImpl;
import com.echo.p2p_project.server.interfaces.ConstructRegistry;
import com.echo.p2p_project.server.interfaces.FileLookupRegistry;
import com.echo.p2p_project.server.interfaces.HeartBeatRegistry;
import com.echo.p2p_project.server.interfaces.HelloRegistryFacade;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;
import com.sun.javafx.collections.ObservableMapWrapper;

import java.io.File;
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
    //The default main server address
    public static String MainServerIP = "127.0.0.1";
    //The default main server port
    public static int RMI_PORT = 1099;
    //Information about this peer
    public static Peer peer;
    public static ObservableMapWrapper<String, Resource> DHRT = new ObservableMapWrapper<>(new LinkedHashMap<>());
    public static Integer retry_times = 0;
    private static String name = "Peer";
    private static String IP = Util.getIP();
    private static Integer Port = 35000;
    private static Integer timeoutMillis = 2000;
    private static Registry registry;
    private static Registry file_service;
    private static ConstructRegistry constructRegistry;
    private static HeartBeatRegistry heartBeatRegistry;
    private static FileLookupRegistry fileLookupRegistry;
    private static Boolean hasStarted = false;
    private static Scanner sc;
    private static Thread service;
    private static Integer download_retry_count = 0;


    public static void main(String[] args) throws ConnectException {
        //service thread
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

        //Command line of the peer
        sc = new Scanner(System.in);
        System.out.println("");
        System.out.print(">>> ");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            switch (line) {
                case "\n":
                    //Promote
                    System.out.print(">>> ");
                    break;
                case "i":
                    //print the information about his peer
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
                    System.out.print("Look up File HASH: ");
                    String hash = sc.nextLine();
                    Resource res = look_up_file(hash);
                    if (res != null)
                        System.out.println(res);
                    break;
                case "d":
                    System.out.print("Download File HASH: ");
                    String file_name_to_download = sc.nextLine();
                    download(file_name_to_download);


            }
            System.out.print(">>> ");
        }
    }

    public static void download(String hash) {
        //download by hash
        //first look up this res
        Resource resources = look_up_file(hash);
        if (resources != null) {
            //found
            System.out.println("Download: " + resources.getGUID());
            download(resources);
        }
        download_retry_count = 0;
    }

    private static void download(String hash, Integer retry_count) {
        //retry download
        if (download_retry_count <= 5) {
            download_retry_count += 1;
            System.out.println("Retrying " + download_retry_count);
            Resource resources = look_up_file(hash);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            download(resources);
        } else {
            System.out.println("Give up download.");
            return;
        }
    }


    public static void init_peer() {
        Boolean center_status = false;
        Boolean file_status = false;
        hasStarted = false;
        try {
            //Connect to the main server
            registry = LocateRegistry.getRegistry(MainServerIP, RMI_PORT);
            //check if connection is successful
            center_status = lookup_registry() && register_peer() ? true : false;
        } catch (RemoteException e) {
//            e.printStackTrace();
        }
        if (peer == null) {
            //can't init peer, then server is not start.
            System.out.println("Server Not Running.");
            exit();
        }

        //start regular heart beat to make sure the peer is alive
        CHeartBeat.StartHeart(heartBeatRegistry, peer.getGUID());

        //Init file services
        try {
            //init local server
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
        //get one available port
        Port = NetUtil.getUsableLocalPort(35000, 45000);
        try {
            //get peer information from server
            peer = constructRegistry.ConstructPeer(name, IP, Port);
            System.out.println("+++++++ Peer Construct Success !  +++++++");
            System.out.println("Peer GUID: " + peer.getGUID());
            System.out.println("Peer: " + peer);
            System.out.println("+++++++     Register Finished     +++++++");
            return true;
        } catch (RemoteException e) {
            //server connect failed
            hasStarted = false;
            System.out.println("register_peer RemoteException");
            e.printStackTrace();
            return false;
        }
    }

    private static Boolean lookup_registry() {
        try {
            //check if server is responding.
            HelloRegistryFacade hello = (HelloRegistryFacade) registry.lookup("HelloRegistry");
            String response = hello.helloWorld(name);
            System.out.println("=======> " + response + " <=======");
            if (response == null) {
                System.out.println("server failed");
                return false;
            }
            // get the server functions
            constructRegistry = (ConstructRegistry) registry.lookup("constructRegistry");
            heartBeatRegistry = (HeartBeatRegistry) registry.lookup("heatBeatRegistry");
            fileLookupRegistry = (FileLookupRegistry) registry.lookup("syncingRegistry");
            return true;

        } catch (NotBoundException | RemoteException e) {
            System.out.println("lookup_registry RemoteException");
//            e.printStackTrace();
            return false;
        }

    }

    public static Boolean reg_file(String name) {
        //register a file
        File file = new File("res/" + name);
        if (!file.exists()) {
            System.out.println("File not exists !");
            System.out.println("This File is not exist in your file system !");
            return false;
        } else {
            System.out.println("File ok.");
            System.out.println("File length: " + file.length());
        }
        //get file hash
        String hash = Util.createSha1(file);
        System.out.println(hash);
        try {
            //send the information about the file to the server
            Resource resource = constructRegistry.ConstructResource(peer.getGUID(), name, hash);
            //save it to local DHRT
            ClientMain.DHRT.put(resource.getGUID(), resource);
//            DHRT.put(resource.getGUID(), resource);
            System.out.println("File Register Success!");
            System.out.println("Resource GUID: " + resource.getGUID());
            //update possessing list
            sync_peer();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("File Register Error !");
            return false;
        }
        return true;
    }

    private static void sync_peer() {
        //update possessing list
        try {
            peer = fileLookupRegistry.syncPeer(peer.getGUID());
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Peer Sync Failed !");
        }
        System.out.println("Peer Sync Finished !");
    }

    public static Resource look_up_file(String hash) {
        //look up a file using hash
        System.out.println("Looking HASH for: " + hash);
        //try to get from local DHRT
        Resource res = ClientMain.DHRT.get(hash);
        if (res == null) {
            //not found in local
            System.out.println("Not found in local DHRT");
            System.out.println("Lookup in UHRT...");
            try {
                //let server find this file
                res = fileLookupRegistry.lookupInUHRT(hash);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (res == null) {
                //not in server UHRT
                System.out.println("Not found in UHRT");
                System.out.println("Abort");
                return null;
            }
            //found the file in server UHRT
            System.out.println("Found in UHRT: " + res.getGUID());
            System.out.println("Update DHRT");
            ClientMain.DHRT.put(res.getGUID(), res);
        }
        else{
            //found in local
            System.out.println("Found in DHRT.");
        }
        return res;
    }

    public static void download(Resource resources) {
        //download by resources
        if (resources == null || resources.possessedBy.size() == 0) {
            //resource is invalid
            System.out.println("Resource can not be download !");
            return;
        }
        //if processed by more than one peer(DHRT), then do sort.
        //Although server has give the best peer, but since the DHRT is not always up-to-date
        //so make sure is the best peer.
        ArrayList<Peer> processed_peers = new ArrayList<>();
        for (Peer p : resources.possessedBy.values()) {
            processed_peers.add(p);
        }
        processed_peers.sort(new Comparator<Peer>() {
            @Override
            public int compare(Peer o1, Peer o2) {
                return o1.getRoutingMetric() - o2.getRoutingMetric();
            }
        });
        Peer best_peer = processed_peers.get(0);
        System.out.println("Best peer is: " + best_peer.getGUID());
        //go download with the best peer
        P2P_download(best_peer, resources);
    }

    public static void P2P_download(Peer p, Resource resource) {
        //download in a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //start download timing
                long start_time = System.currentTimeMillis();
                Integer port = p.getP2P_port();
                String IP = p.getIP();
                Registry p2pRegistry = null;
                File file = null;
                try {
                    //get target peer connection
                    p2pRegistry = LocateRegistry.getRegistry(IP, port);
                    System.out.println("Try to connect:   " + p.getGUID());
                    P2P_FileRegistry p2PFileRegistry = (P2P_FileRegistry) p2pRegistry.lookup("p2PFileRegistry");
                    System.out.println("Connected.");
                    System.out.println("Downloading FROM: " + p.getGUID());
                    byte[] file_byte = p2PFileRegistry.download(resource.getGUID());
                    System.out.println("File byte[] length: " + file_byte.length);
                    //end download timing
                    long end_time = System.currentTimeMillis();
                    long start_writing_time = System.currentTimeMillis();
                    //write to file
                    file = FileUtil.writeBytes(file_byte, new File("download/" + peer.getGUID() + "_FROM_" + p.getGUID() + "_" + resource.getName()));
                    long end_writing_time = System.currentTimeMillis();
                    System.out.println("File downloaded !");
                    System.out.println("File length : " + file.length());

                    //display the result regarding the file size.
                    float file_size_B = file.length();
                    float file_size_KB = file.length() / 1024;
                    float file_size_MB = file_size_KB / 1024;
                    if (file_size_KB <= 1) {
                        System.out.println("################# Download Finished #################");
                        System.out.println("Network COST: " + (end_time - start_time) + " ms.");
                        System.out.println("Disk COST: " + (end_writing_time - start_writing_time) + " ms.");
                        System.out.println("DATA Size: " + file_size_B + " Byte.");
                        System.out.println("Average Speed: " + file_size_B / ((end_time - start_time) / 1000) + " Byte/s.");
                        System.out.println("#####################################################");
                    } else {
                        if (file_size_MB <= 1) {
                            System.out.println("################# Download Finished #################");
                            System.out.println("COST: " + (end_time - start_time) + " ms.");
                            System.out.println("Disk COST: " + (end_writing_time - start_writing_time) + " ms.");
                            System.out.println("DATA Size: " + file_size_KB + " KB.");
                            System.out.println("Average Speed: " + file_size_KB / ((end_time - start_time) / 1000) + " KB/s.");
                            System.out.println("#####################################################");
                        } else {
                            System.out.println("################# Download Finished #################");
                            System.out.println("COST: " + (end_time - start_time) / 1000 + " s.");
                            System.out.println("Disk COST: " + (end_writing_time - start_writing_time) + " ms.");
                            System.out.println("DATA Size: " + file_size_MB + " MB.");
                            System.out.println("Average Speed: " + file_size_MB / ((end_time - start_time) / 1000) + " MB/s.");
                            System.out.println("#####################################################");
                        }
                    }

                } catch (RemoteException e) {
                    //download failed.
                    System.out.println("RemoteException");
                    DHRT.clear();
                    try {
                        //do re-lookup
                        Resource res = fileLookupRegistry.lookupInUHRT(resource.getGUID());
                        if (res != null)
                            DHRT.put(res.getGUID(), res);
                        System.out.println("Try to update DHRT.");
                        download(resource.getGUID(), download_retry_count);
                        return;
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void P2P_download(Peer p, Resource resource, Runnable runnable) {
        //same as the upper function, but with a customizable runnable after the file downloaded
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start_time = System.currentTimeMillis();
                Integer port = p.getP2P_port();
                String IP = p.getIP();
                Registry p2pRegistry = null;
                File file = null;
                try {
                    p2pRegistry = LocateRegistry.getRegistry(IP, port);
                    System.out.println("Try to connect:   " + p.getGUID());
                    P2P_FileRegistry p2PFileRegistry = (P2P_FileRegistry) p2pRegistry.lookup("p2PFileRegistry");
                    System.out.println("Connected.");
                    System.out.println("Downloading FROM: " + p.getGUID());
                    byte[] file_byte = p2PFileRegistry.download(resource.getGUID());
                    System.out.println("File byte[] length: " + file_byte.length);
                    long end_time = System.currentTimeMillis();
                    long start_writing_time = System.currentTimeMillis();
                    file = FileUtil.writeBytes(file_byte, new File("download/" + peer.getGUID() + "_FROM_" + p.getGUID() + "_" + resource.getName()));
                    long end_writing_time = System.currentTimeMillis();
                    System.out.println("File downloaded !");
                    System.out.println("File length : " + file.length());

                    float file_size_B = file.length();
                    float file_size_KB = file.length() / 1024;
                    float file_size_MB = file_size_KB / 1024;
                    if (file_size_KB <= 1) {
                        System.out.println("################# Download Finished #################");
                        System.out.println("Network COST: " + (end_time - start_time) + " ms.");
                        System.out.println("Disk COST: " + (end_writing_time - start_writing_time) + " ms.");
                        System.out.println("DATA Size: " + file_size_B + " Byte.");
                        System.out.println("Average Speed: " + file_size_B / ((end_time - start_time) / 1000) + " Byte/s.");
                        System.out.println("#####################################################");
                    } else {
                        if (file_size_MB <= 1) {
                            System.out.println("################# Download Finished #################");
                            System.out.println("COST: " + (end_time - start_time) + " ms.");
                            System.out.println("Disk COST: " + (end_writing_time - start_writing_time) + " ms.");
                            System.out.println("DATA Size: " + file_size_KB + " KB.");
                            System.out.println("Average Speed: " + file_size_KB / ((end_time - start_time) / 1000) + " KB/s.");
                            System.out.println("#####################################################");
                        } else {
                            System.out.println("################# Download Finished #################");
                            System.out.println("COST: " + (end_time - start_time) / 1000 + " s.");
                            System.out.println("Disk COST: " + (end_writing_time - start_writing_time) + " ms.");
                            System.out.println("DATA Size: " + file_size_MB + " MB.");
                            System.out.println("Average Speed: " + file_size_MB / ((end_time - start_time) / 1000) + " MB/s.");
                            System.out.println("#####################################################");
                        }
                    }

                } catch (RemoteException e) {
                    System.out.println("RemoteException");
                    DHRT.clear();
                    try {
                        Resource res = fileLookupRegistry.lookupInUHRT(resource.getGUID());
                        if (res != null)
                            DHRT.put(res.getGUID(), res);
                        download(resource.getGUID(), download_retry_count);
                        return;
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
                runnable.run();
            }
        }).start();
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
//                DHRT = syncingRegistry.lookupInUHRT();
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
        //self-recovery
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
                    //wait 1s then try again.
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
