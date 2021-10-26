package com.echo.p2p_project.server.model;

import com.echo.p2p_project.server.ServerMain;
import com.echo.p2p_project.server.interfaces.ConstructRegistry;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-17:10
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.model
 * @Description:
 **/
public class ConstructImpl extends UnicastRemoteObject implements ConstructRegistry {
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
    public ConstructImpl() throws RemoteException {
        super();
    }

    @Override
    public Peer ConstructPeer(String name, String IP_Address, Integer P2P_port) throws RemoteException {
        UUID GUID = UUID.randomUUID();
        if(ServerMain.UHPT.containsKey(GUID)) {
            System.out.println("DUP GUID!!!!!!!");
            return null;
        }
        String peerName = name;
        String IP = IP_Address;
        Integer port = P2P_port;
        Random random = new Random();
        Integer routingMetric = random.nextInt(100);
        Peer peer = new Peer(GUID, peerName, IP, port, routingMetric);
        ServerMain.UHPT.put(peer.getGUID(), peer);
        System.out.println("Peer Reg: " + peer);
        return peer;
    }

    @Override
    public Resource ConstructResource(UUID PeerGUID, String name, String hash) throws RemoteException {
        Peer peer = ServerMain.UHPT.get(PeerGUID);

        //If peer not registered in center
        if(peer == null)
            return null; // Res register failed


        UUID GUID = UUID.randomUUID();
        //If duplicated GUID
        if(ServerMain.UHPT.containsKey(GUID)) {
            System.out.println("DUP GUID!!!!!!!");
            return null; // Res register failed
        }

        String ResName = name;
        Resource res = new Resource(GUID, ResName, hash);
        res.possessedBy.put(peer.getGUID(), peer);
        ServerMain.UHRT.put(res.getGUID(), res);
        ServerMain.UHPT.get(peer.getGUID()).possessing.put(res.getGUID(), res);
        System.out.println("Resources Registered: " + res);
        return res;
    }
}
