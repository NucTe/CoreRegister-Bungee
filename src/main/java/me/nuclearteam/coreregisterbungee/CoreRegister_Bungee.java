package me.nuclearteam.coreregisterbungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public final class CoreRegister_Bungee extends Plugin implements Listener {

    private Set<String> registeredPlugins;


    @Override
    public void onEnable() {
        registeredPlugins = new HashSet<>();

        // Register the PluginMessageListener
        getProxy().registerChannel("BungeeCord");
        getProxy().getPluginManager().registerListener(this, this);

    }

    @Override
    public void onDisable(){
        getProxy().unregisterChannel("BungeeCord");
    }

    public void registerPlugin(String pluginName) {
        registeredPlugins.add(pluginName);
        // Broadcast to all servers that this plugin has been registered
        sendRegistrationMessage(pluginName);
    }

    public void unregisterPlugin(String pluginName) {
        registeredPlugins.remove(pluginName);
        // Broadcast to all servers that this plugin has been unregistered
        sendUnregistrationMessage(pluginName);
    }

    private void sendRegistrationMessage(String pluginName) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            // Specify the subchannel and additional data
            String message = "REGISTER:" + pluginName;

            // Write the subchannel and message to the output stream
            out.writeUTF("Register");
            out.writeUTF(message);

            // Send the data on the "BungeeCord" channel
            player.getServer().sendData("BungeeCord", out.toByteArray());
            getLogger().info(message);
        }
    }


    private void sendUnregistrationMessage(String pluginName) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.getServer().sendData("BungeeCord", ("UNREGISTER:" + pluginName).getBytes());
        }
    }
}
