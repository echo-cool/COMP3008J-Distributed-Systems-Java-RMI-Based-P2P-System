package com.echo.p2p_project.u_model;

import cn.hutool.core.lang.hash.Hash128;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/20-16:57
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.u_model
 * @Description:
 **/
public class Resource implements Serializable {
    private String GUID;
    private String name;
    public HashMap<UUID, Peer> possessedBy = new LinkedHashMap<>();
    public String hash;

    public Resource(String GUID, String name) {
        this.GUID = GUID;
        this.name = name;
    }

    public Resource(String GUID, String name, String hash) {
        this.GUID = GUID;
        this.name = name;
        this.hash = hash;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<UUID, Peer> getPossessedBy() {
        return possessedBy;
    }

    public void setPossessedBy(HashMap<UUID, Peer> possessedBy) {
        this.possessedBy = possessedBy;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "GUID=" + GUID +
                ", name='" + name + '\'' +
                ", possessedBy=" + possessedBy.keySet() +
                ", hash='" + hash + '\'' +
                '}';
    }
}
