package net.miaomoe.blocker.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.miaomoe.blessing.config.type.ConfigType;
import net.miaomoe.blessing.config.util.SimpleConfigUtil;
import net.miaomoe.blocker.BlockerVelocity;
import net.miaomoe.blocker.config.Group;

@RequiredArgsConstructor
public class MainCommand implements RawCommand {
    private final BlockerVelocity plugin;
    @Override
    public void execute(Invocation invocation) {
        final MiniMessage miniMessage = plugin.getMiniMessage();
        final CommandSource source = invocation.source();
        try {
            SimpleConfigUtil.saveAndRead(plugin.getDataDirectory().toFile(), "config", plugin.getRootConfig(), ConfigType.HOCON);
            if (plugin.getRootConfig().isDebug()) {
                for (final Group group : plugin.getRootConfig().getGroups()) plugin.getLogger().info("Group: {}", group);
            }
            source.sendMessage(miniMessage.deserialize("<green>Reloaded."));
        } catch (Exception e) {
            source.sendMessage(miniMessage.deserialize("<red>Could not load config! See console for more information."));
            plugin.getLogger().warn("Failed to load config", e);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("blocker.command");
    }
}
