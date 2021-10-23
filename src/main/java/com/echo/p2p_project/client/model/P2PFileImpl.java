package com.echo.p2p_project.client.model;

import com.echo.p2p_project.client.ClientMain;
import com.echo.p2p_project.client.interfaces.P2P_FileRegistry;
import com.echo.p2p_project.u_model.Resource;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-21:08
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.client.model
 * @Description:
 **/
public class P2PFileImpl extends UnicastRemoteObject implements P2P_FileRegistry {
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
    public P2PFileImpl() throws RemoteException {
        super();
    }


    @Override
    public File download(UUID resID) throws RemoteException{
        Resource resource = ClientMain.DHRT.get(resID);
        if(resource == null){
            System.out.println("Resource Not in local DHRT.");
            return null;
        }
        File file = new File("res/" + resource.getName());
        if (file==null) {
            System.out.println("Resource Not in File System.");
            return null;
        }
        return file;
    }
}
