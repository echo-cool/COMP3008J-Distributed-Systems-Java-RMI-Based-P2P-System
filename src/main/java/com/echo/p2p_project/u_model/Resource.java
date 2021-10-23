package com.echo.p2p_project.u_model;

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
    private UUID GUID;
    private String name;
    public HashMap<UUID, Peer> possessedBy = new LinkedHashMap<>();

    public Resource(UUID GUID, String name) {
        this.GUID = GUID;
        this.name = name;
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

    public HashMap<UUID, Peer> getPossessedBy() {
        return possessedBy;
    }

    public void setPossessedBy(HashMap<UUID, Peer> possessedBy) {
        this.possessedBy = possessedBy;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "GUID=" + GUID +
                ", name='" + name + '\'' +
                ", possessedBy=" + possessedBy.keySet() +
                '}';
    }
}
