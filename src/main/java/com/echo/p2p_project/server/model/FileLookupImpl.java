package com.echo.p2p_project.server.model;

import com.echo.p2p_project.server.ServerMain;
import com.echo.p2p_project.server.interfaces.FileLookupRegistry;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-22:45
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.model
 * @Description:
 **/
public class FileLookupImpl extends UnicastRemoteObject implements FileLookupRegistry {
    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     *
     * <p>The object is exported with a server socket
     * created using the {@link RMISocketFactory} class.
     *
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    public FileLookupImpl() throws RemoteException {
        super();
    }

    @Override
    public Peer syncPeer(UUID GUID) throws RemoteException {
        Peer peer = ServerMain.UHPT.get(GUID);
        if (peer == null)
            return null;
        return peer;
    }

    @Override
    public Resource lookupInUHRT(String hash) throws RemoteException {

        Resource res = ServerMain.UHRT.get(hash);
        if(res==null){
            return null;
        }
        ArrayList<Peer> processed_peers = new ArrayList<>();
        for (Peer p : res.possessedBy.values()) {
            processed_peers.add(p);
        }
        processed_peers.sort(new Comparator<Peer>() {
            @Override
            public int compare(Peer o1, Peer o2) {
                return o1.getRoutingMetric() - o2.getRoutingMetric();
            }
        });
        Peer best_peer = processed_peers.get(0);
        for (Peer peer: processed_peers) {
            System.out.println(peer.getGUID() + "   <>   " + peer.getRoutingMetric());
        }
        System.out.println("BEST: " + best_peer.getGUID());
        Resource tmp = new Resource(res.getGUID(), res.getName(), res.getHash());
        tmp.possessedBy.put(best_peer.getGUID(), best_peer);

        return tmp;
    }


//    @Override
//    public HashMap lookupInUHRT(String filename) throws RemoteException {
//        HashMap<UUID, Resource> hashMap = new LinkedHashMap();
//
//        for (UUID key : ServerMain.UHRT.keySet()) {
//            if (ServerMain.UHRT.get(key).getName().equals(filename)) {
//                hashMap.put(key, ServerMain.UHRT.get(key));
//            }
//        }
//
//        System.out.println("Checking file hashing...");
//        Boolean hash_all_same = true;
//        String file_hash = ((Resource) hashMap.values().toArray()[0]).getHash();
//        for (Resource r : hashMap.values()) {
//            if (!r.getHash().equals(file_hash)) {
//                hash_all_same = false;
//                break;
//            }
//        }
//        UUID GUID = null;
//        if (hash_all_same) {
//            System.out.println("Hash is the same, looking for best node...");
//            ArrayList<Peer> processed_peers = new ArrayList<>();
//            for (Resource r : hashMap.values()) {
//                for (Peer p : r.possessedBy.values()) {
//                    processed_peers.add(p);
//                }
//            }
//            processed_peers.sort(new Comparator<Peer>() {
//                @Override
//                public int compare(Peer o1, Peer o2) {
//                    return o1.getRoutingMetric() - o2.getRoutingMetric();
//                }
//            });
//            for (Peer p : processed_peers) {
//                System.out.println(p.getGUID().toString() + "  <>  " + p.getRoutingMetric());
//            }
//
//            for (Resource r : processed_peers.get(0).possessing.values()) {
//                System.out.println("File hash: " + file_hash);
//                System.out.println("Res hash : " + r.getHash());
//                if (r.getHash().equals(file_hash)) {
//                    System.out.println("Best resource GUID: " + r.getGUID());
//                    System.out.println("Best Peer GUID: " + processed_peers.get(0).getGUID());
//                    GUID = r.getGUID();
//                    HashMap tmp = new LinkedHashMap();
//                    tmp.put(GUID, ServerMain.UHRT.get(GUID));
//                    return tmp;
//                }
//            }
//        }
//
//        return hashMap;
//    }
}
