package com.echo.p2p_project;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-17:39
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project
 * @Description:
 **/
public class Util {
    public static final int RMI_PORT = 1099;

    public static String getIP() {
        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            return ip4.getHostAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

}
