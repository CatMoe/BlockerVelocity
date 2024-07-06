package net.miaomoe.blocker;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.network.Connections;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.miaomoe.blessing.config.type.ConfigType;
import net.miaomoe.blessing.config.util.SimpleConfigUtil;
import net.miaomoe.blocker.command.MainCommand;
import net.miaomoe.blocker.config.Group;
import net.miaomoe.blocker.config.RootConfig;
import net.miaomoe.blocker.handler.PacketInboundHandler;
import net.miaomoe.blocker.handler.PacketOutboundHandler;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
@Plugin(
        id = "blockervelocity",
        name = "BlockerVelocity",
        version = BuildConstants.VERSION
)
@Getter
public final class BlockerVelocity {

    @Inject private Logger logger;
    @Inject private ProxyServer proxy;
    @Inject @DataDirectory private Path dataDirectory;
    private RootConfig rootConfig;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        rootConfig = new RootConfig(this);
        proxy.getCommandManager().register("blockervelocity", new MainCommand(this));
        try {
            SimpleConfigUtil.saveAndRead(dataDirectory.toFile(), "config", rootConfig, ConfigType.HOCON);
            if (getRootConfig().isDebug()) {
                for (final Group group : getRootConfig().getGroups()) getLogger().info("Group: {}", group);
            }
        } catch (final Exception e) {
            getLogger().warn("Failed to load config. Using default config.", e);
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerJoin(final PostLoginEvent event) {
        if (event.getPlayer() instanceof ConnectedPlayer player) {
            if (rootConfig.isDebug()) getLogger().info("Injecting Channel for player {}", player.getGameProfile().getName());
            final ChannelPipeline pipeline = player.getConnection().getChannel().pipeline();
            if (pipeline.get(PacketInboundHandler.NAME) == null) {
                pipeline.addAfter(Connections.MINECRAFT_DECODER, PacketInboundHandler.NAME, new PacketInboundHandler(this, player));
            }
            if (pipeline.get(PacketOutboundHandler.NAME) == null) {
                pipeline.addAfter(Connections.MINECRAFT_ENCODER, PacketOutboundHandler.NAME, new PacketOutboundHandler(this, player));
            }
            if (rootConfig.isDebug()) {
                final Set<String> pipelines = new HashSet<>();
                for (final Map.Entry<String, ChannelHandler> entry : pipeline) {
                    pipelines.add(entry.getKey());
                }
                getLogger().info("Pipelines: {}", pipelines);
            }
        }
    }
}
