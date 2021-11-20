package com.echo.p2p_project.server.model;

import com.echo.p2p_project.server.interfaces.HelloRegistryFacade;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-16:38
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.model
 * @Description:
 **/
public class HelloRegistryFacadeImpl extends UnicastRemoteObject implements HelloRegistryFacade {


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
    public HelloRegistryFacadeImpl() throws RemoteException {
        super();
        //a test.
        System.out.println("HelloRegistryFacadeImpl");
    }

    @Override
    public String helloWorld(String name) {
        return "[Registry] Hi, " + name;
    }

}