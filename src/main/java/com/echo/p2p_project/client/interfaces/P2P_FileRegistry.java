package com.echo.p2p_project.client.interfaces;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-21:07
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.client.interfaces
 * @Description:
 **/
public interface P2P_FileRegistry extends Remote {
    byte[] download(String resID) throws RemoteException;
}
