package net.miaomoe.blocker.handler;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.TabCompleteRequestPacket;
import com.velocitypowered.proxy.protocol.packet.TabCompleteResponsePacket;
import com.velocitypowered.proxy.protocol.packet.chat.legacy.LegacyChatPacket;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerCommandPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.miaomoe.blocker.BlockerVelocity;
import org.jetbrains.annotations.NotNull;

@Value
@ChannelHandler.Sharable
@EqualsAndHashCode(callSuper = false)
public class PacketInboundHandler extends ChannelInboundHandlerAdapter {
    public static final String NAME = "velocity-blocker:inbound-handler";

    @NotNull BlockerVelocity plugin;
    @NotNull ConnectedPlayer player;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TabCompleteRequestPacket packet && packet.getCommand().startsWith("/")) {
            if (plugin.getRootConfig().cancel(player, packet.getCommand(), true)) {
                final TabCompleteResponsePacket response = new TabCompleteResponsePacket();
                response.setTransactionId(packet.getTransactionId());
                response.setStart(0);
                response.setLength(0);
                response.getOffers().clear();
                player.getConnection().write(response);
                return;
            }
        } else if (msg instanceof SessionPlayerCommandPacket packet) {
            if (plugin.getRootConfig().cancel(player, packet.getCommand())) return;
        } else if (msg instanceof LegacyChatPacket packet && packet.getMessage().startsWith("/")) {
            if (plugin.getRootConfig().cancel(player, packet.getMessage())) return;
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (plugin.getRootConfig().isDebug()) cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
