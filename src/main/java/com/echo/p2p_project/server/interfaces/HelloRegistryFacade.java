package com.echo.p2p_project.server.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-16:37
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.interfaces
 * @Description:
 **/
public interface HelloRegistryFacade extends Remote {
    String helloWorld(String name) throws RemoteException;
}
