package com.echo.p2p_project.u_model;

import cn.hutool.Hutool;
import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/19-15:23
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.u_model
 * @Description:
 **/
public class Peer implements Serializable,Comparable {
    //All the information about the Peer.
    private UUID GUID;
    private String name;
    private String IP;
    private Integer P2P_port;
    private Integer routingMetric;
    private Integer MissedHartBeat = 0;
    //Only use this possessing in DHRT
    public HashMap<String, Resource> possessing = new LinkedHashMap();

    public Peer(UUID GUID, String name, String IP, Integer p2P_port, Integer routingMetric) {
        this.GUID = GUID;
        this.name = name;
        this.IP = IP;
        P2P_port = p2P_port;
        this.routingMetric = routingMetric;
    }

    public UUID getGUID() {
        return GUID;
    }

    public void setGUID(UUID GUID) {
        this.GUID = GUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Integer getP2P_port() {
        return P2P_port;
    }

    public void setP2P_port(Integer p2P_port) {
        P2P_port = p2P_port;
    }

    public Integer getRoutingMetric() {
        return routingMetric;
    }

    public void setRoutingMetric(Integer routingMetric) {
        this.routingMetric = routingMetric;
    }

    public HashMap<String, Resource> getPossessing() {
        return possessing;
    }

    public void setPossessing(HashMap<String, Resource> possessing) {
        this.possessing = possessing;
    }

    public Integer getMissedHartBeat() {
        return MissedHartBeat;
    }

    public void setMissedHartBeat(Integer missedHartBeat) {
        MissedHartBeat = missedHartBeat;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "GUID=" + GUID +
                ", name='" + name + '\'' +
                ", IP='" + IP + '\'' +
                ", P2P_port=" + P2P_port +
                ", routingMetric=" + routingMetric +
                ", MissedHartBeat=" + MissedHartBeat +
                ", possessing=" + possessing.keySet() +
                '}';
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(@NotNull Object o) {
        Peer p = (Peer) o;
        return p.routingMetric - this.routingMetric;
    }
}
