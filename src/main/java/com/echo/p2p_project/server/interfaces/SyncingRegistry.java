package com.echo.p2p_project.server.interfaces;

import com.echo.p2p_project.u_model.Peer;

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
public interface SyncingRegistry extends Remote {
    Peer syncPeer(UUID GUID) throws RemoteException;
    HashMap syncUHRT() throws RemoteException;
}
