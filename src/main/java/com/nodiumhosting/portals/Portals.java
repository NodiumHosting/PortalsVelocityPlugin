package com.nodiumhosting.portals;

import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import org.slf4j.Logger;

import java.util.Arrays;

@Plugin(id = "portals", name = "Portals", version = BuildConstants.VERSION)
public class Portals {

    @Inject
    private Logger LOGGER;
    private ProxyServer proxy;

    private LegacyChannelIdentifier MSG_CHANNEL;

    @Inject
    public Portals(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.LOGGER = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        MSG_CHANNEL = new LegacyChannelIdentifier("portals:velocity_transfer");

        proxy.getChannelRegistrar().register(MSG_CHANNEL);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().equals(MSG_CHANNEL)) {
            if (event.getSource() instanceof ServerConnection serverConnection) {
                LOGGER.info(event.getIdentifier().getId());
                LOGGER.info(new String(event.getData()));
            }
            // So that client packets don't make it through to the servers,
            // always trigger on this channel.
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }
    }
}
