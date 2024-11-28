package com.nodiumhosting.portals;

import com.google.gson.Gson;

public class VelocityTransferPacket {
    public String player;
    public String server;

    public static VelocityTransferPacket decode(byte[] data) {
        String json = new String(data);
        Gson gson = new Gson();
        VelocityTransferPacket packet = gson.fromJson(json, VelocityTransferPacket.class);
        return packet;
    }
}