package net.miaomoe.blocker.handler;

import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.packet.AvailableCommandsPacket;
import com.velocitypowered.proxy.protocol.packet.TabCompleteResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.miaomoe.blocker.BlockerVelocity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Map;

@Value
@ChannelHandler.Sharable
@EqualsAndHashCode(callSuper = false)
public class PacketOutboundHandler extends ChannelOutboundHandlerAdapter {
    public static final String NAME = "velocity-blocker:outbound-handler";

    @NotNull BlockerVelocity plugin;
    @NotNull ConnectedPlayer player;

    private static @Nullable MethodHandle CHILDREN_GETTER;

    static {
        try {
            CHILDREN_GETTER = MethodHandles
                    .privateLookupIn(CommandNode.class, MethodHandles.lookup())
                    .findGetter(CommandNode.class, "children", Map.class);
        } catch (final Throwable e) {
            CHILDREN_GETTER = null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof MinecraftPacket) {
            if (msg instanceof TabCompleteResponsePacket packet) {
                plugin.getRootConfig().removeBlocked(player, packet);
            } else if (msg instanceof AvailableCommandsPacket packet) {
                if (CHILDREN_GETTER != null) {
                    try {
                        final HashSet<String> toRemove = new HashSet<>();
                        final Map<String, CommandNode<?>> map = (Map<String, CommandNode<?>>)
                                CHILDREN_GETTER.invokeExact((CommandNode<?>) packet.getRootNode());
                        for (final Map.Entry<String, CommandNode<?>> entry : map.entrySet()) {
                            if (plugin.getRootConfig().cancel(player, entry.getKey(), true))
                                toRemove.add(entry.getKey());
                        }
                        toRemove.forEach(map::remove);
                    } catch (final Throwable e) {
                        if (plugin.getRootConfig().isDebug()) {
                            plugin.getLogger().warn("Failed to invoke children map", e);
                        }
                    }
                }
            }
        }
        super.write(ctx, msg, promise);
    }
}
