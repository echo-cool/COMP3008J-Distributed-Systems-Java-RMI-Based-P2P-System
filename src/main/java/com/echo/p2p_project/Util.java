package com.echo.p2p_project;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    public static String createSha1(File file){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            try {
                n = fis.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return new HexBinaryAdapter().marshal(digest.digest());
    }

}
