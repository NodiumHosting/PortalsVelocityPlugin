package com.nodiumhosting.portals;

import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = "portals", name = "Portals", version = BuildConstants.VERSION)
public class Portals {
    public static Logger LOGGER;
    public static ProxyServer PROXY;

    private LegacyChannelIdentifier MSG_CHANNEL;

    @Inject
    public Portals(ProxyServer proxy, Logger logger) {
        PROXY = proxy;
        LOGGER = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        MSG_CHANNEL = new LegacyChannelIdentifier("portals:velocity_transfer");

        PROXY.getChannelRegistrar().register(MSG_CHANNEL);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().equals(MSG_CHANNEL)) {
            if (event.getSource() instanceof ServerConnection serverConnection) {
                //remove first two bytes because it's the packet id
                byte[] data = Arrays.copyOfRange(event.getData(), 2, event.getData().length);
                VelocityTransferPacket packet = VelocityTransferPacket.decode(data);

                Optional<Player> player = PROXY.getPlayer(UUID.fromString(packet.player));
                if (player.isEmpty()) {
                    return;
                } else {
                    LOGGER.info("Received transfer request for player {} to server {}", player.get().getUsername(), packet.server);
                }

                Optional<RegisteredServer> server = PROXY.getServer(packet.server);
                if (server.isEmpty()) {
                    LOGGER.warn("Server {} not found", packet.server);
                    return;
                }

                player.get().createConnectionRequest(server.get()).fireAndForget();
            }
            // So that client packets don't make it through to the servers,
            // always trigger on this channel.
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }
    }
}
