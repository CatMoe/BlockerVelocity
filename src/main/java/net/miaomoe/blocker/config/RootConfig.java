package net.miaomoe.blocker.config;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.protocol.packet.TabCompleteResponsePacket;
import com.velocitypowered.proxy.protocol.packet.TabCompleteResponsePacket.Offer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.miaomoe.blessing.config.annotation.ConfigValue;
import net.miaomoe.blessing.config.parser.AbstractConfig;
import net.miaomoe.blocker.BlockerVelocity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RequiredArgsConstructor
public final class RootConfig extends AbstractConfig {
    @ConfigValue
    @Getter @Setter private boolean debug = false;
    private final @NotNull BlockerVelocity plugin;
    @ConfigValue @Getter
    private @NotNull List<Group> groups = Collections.singletonList(new Group());

    public boolean cancel(final @NotNull Player player, final @NotNull String value, final boolean silent) {
        if (debug) plugin.getLogger().info("User: {} - Native Input: {}", player.getUniqueId(), value);
        for (final @NotNull Group group : groups) {
            if (player.hasPermission("blocker.bypass." + group.getName()) || !player.hasPermission("blocker.group." + group.getName())) continue;
            if (cancel(group, value)) {
                if (debug) plugin.getLogger().info("Blocked: {} - {} with group rule: {}", player.getUniqueId(), value, group.getName());
                if (!silent) {
                    for (final String toSend : group.getBlockedTip()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(toSend, Placeholder.unparsed("command", value)));
                    }
                }
                 return true;
            }
        }
        if (debug) plugin.getLogger().info("Success: {} - {}", player.getUniqueId(), value);
        return false;
    }

    public void removeBlocked(final @NotNull Player player, final @NotNull TabCompleteResponsePacket packet) {
        final Map<String, Offer> map = new HashMap<>();
        final Set<String> set = new HashSet<>();
        for (final Offer offer : packet.getOffers()) {
            map.put(offer.getText(), offer);
            set.add(offer.getText());
        }
        if (debug) plugin.getLogger().info("User: {} - Output Tab Complete: {} - {}", player.getUniqueId(), packet.getTransactionId(), set);
        for (final @NotNull Group group : groups) {
            if (player.hasPermission("blocker.bypass." + group.getName()) || !player.hasPermission("blocker.group." + group.getName())) continue;
            final Set<String> toRemove = new HashSet<>();
            for (final @NotNull String complete : set) {
                if (cancel(group, complete)) {
                    if (debug) plugin.getLogger().info("Removed from tab complete: {} - {} - {}", player.getUniqueId(), complete, packet.getTransactionId());
                    toRemove.add(complete);
                }
            }
            toRemove.forEach(set::remove);
        }
        final Collection<Offer> values = set.stream().map(map::get).filter(Objects::nonNull).toList();
        packet.getOffers().clear();
        packet.getOffers().addAll(values);
        //packet.setLength(values.size());
    }

    public boolean cancel(final @NotNull Player player, final @NotNull String value) {
        return this.cancel(player, value, false);
    }

    public boolean cancel(
            final @NotNull Group group,
            final @Nullable String value
    ) {
        @NotNull String message = (value == null ? "" : value).toLowerCase(Locale.ROOT);
        if (message.contains("/")) message=message.replaceFirst("/", "");
        if (message.contains(" ")) message=message.split(" ")[0];
        if (message.contains(":")) {
            final String[] split = message.split(":");
            if (split.length >= 2) {
                return cancel(group, split[1]) || cancel(group, split[0]);
            } else return cancel(group, message.replace(":", ""));
        } else {
            for (final @NotNull String toIgnore : group.getCommands()) {
                if (message.equals(toIgnore.toLowerCase(Locale.ROOT))) {
                    if (debug) plugin.getLogger().info("Matched: {} == {}", value, toIgnore);
                    return true;
                }
            }
        }
        return false;
    }
}
