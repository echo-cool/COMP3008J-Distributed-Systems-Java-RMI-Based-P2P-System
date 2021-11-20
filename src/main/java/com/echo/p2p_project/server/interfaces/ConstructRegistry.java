package com.echo.p2p_project.server.interfaces;

import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-17:09
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.interfaces
 * @Description:
 **/
public interface ConstructRegistry extends Remote {
    //saving the peer information and transfer back to the peer.
    Peer ConstructPeer(String name, String IP, Integer P2P_port) throws RemoteException;
    //saving the resource information and transfer back to the peer.
    Resource ConstructResource(UUID PeerGUID, String name, String hash) throws RemoteException;
}
