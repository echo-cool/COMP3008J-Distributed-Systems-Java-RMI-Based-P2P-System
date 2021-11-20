package com.echo.p2p_project.server.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-17:48
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.interfaces
 * @Description:
 **/
public interface HeartBeatRegistry extends Remote {
    //make sure the peer is alive
    Boolean heartBeat(UUID GUID) throws RemoteException;
}
