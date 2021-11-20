package com.echo.p2p_project.server.interfaces;

import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-22:45
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.interfaces
 * @Description:
 **/
public interface FileLookupRegistry extends Remote {
    //update the peer's possessing list
    Peer syncPeer(UUID GUID) throws RemoteException;
    //lookup in server UHRT using the GUID(hash)
    Resource lookupInUHRT(String hash) throws RemoteException;
}
