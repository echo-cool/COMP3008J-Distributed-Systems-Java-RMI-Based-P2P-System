package com.echo.p2p_project.server.model;

import com.echo.p2p_project.server.ServerMain;
import com.echo.p2p_project.server.interfaces.HeartBeatRegistry;
import com.echo.p2p_project.u_model.Peer;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-17:49
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.model
 * @Description:
 **/
public class HeartBeatImpl extends UnicastRemoteObject implements HeartBeatRegistry {
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
    public HeartBeatImpl() throws RemoteException {
        super();
    }

    @Override
    public Boolean heartBeat(UUID GUID) throws RemoteException {
        Peer peer = ServerMain.UHPT.get(GUID);
        if(peer == null)
            return false;
        peer.setMissedHartBeat(0);
        return true;
    }
}
