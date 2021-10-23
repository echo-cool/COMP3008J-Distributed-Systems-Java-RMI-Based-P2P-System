package com.echo.p2p_project.server.model;

import com.echo.p2p_project.server.ServerMain;
import com.echo.p2p_project.server.interfaces.SyncingRegistry;
import com.echo.p2p_project.u_model.Peer;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-22:45
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.model
 * @Description:
 **/
public class SyncingImpl extends UnicastRemoteObject implements SyncingRegistry {
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
    public SyncingImpl() throws RemoteException {
        super();
    }

    @Override
    public Peer syncPeer(UUID GUID) throws RemoteException{
        Peer peer = ServerMain.UHPT.get(GUID);
        if (peer==null)
            return null;
        return peer;
    }

    @Override
    public HashMap syncUHRT() throws RemoteException {
        HashMap hashMap = new LinkedHashMap();
        for (UUID key: ServerMain.UHRT.keySet()) {
            hashMap.put(key, ServerMain.UHRT.get(key));
        }
        HashMap map = hashMap;
        return map;
    }
}
