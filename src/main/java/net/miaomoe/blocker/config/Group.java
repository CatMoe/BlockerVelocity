package net.miaomoe.blocker.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.miaomoe.blessing.config.annotation.Comment;
import net.miaomoe.blessing.config.annotation.ParseAllField;
import net.miaomoe.blessing.config.parser.AbstractConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@ParseAllField
@SuppressWarnings("SpellCheckingInspection")
public final class Group extends AbstractConfig {
    private String name = "default";
    @Comment(description = {
            "Commands to block. (command & tab complete)",
            "matrix will blocked /matrix & /matrix:matrix (i.e. No matter what plugin is used as a prefix, it will be blocked)",
            "Empty is \"/\". Block it to prevent tab complete."
    })
    private List<String> commands = Arrays.asList(
            "",
            "grim",
            "grimac",
            "lpv",
            "lp",
            "vulcan",
            "verbose",
            "jday",
            "logs",
            "alerts",
            "nocheatplus",
            "ncp",
            "matrix",
            "coc",
            "karhu",
            "spiter",
            "spartan",
            "bukkit",
            "ver",
            "version",
            "?",
            "icanhasbukkit",
            "about",
            "pl",
            "plugins",
            "plugin",
            "me",
            "fawe",
            "fastasyncworldedit",
            "worldedit",
            "we",
            "calc",
            "eval",
            "for",
            "sudo",
            "blockervelocity"
    );
    private List<String> blockedTip = Collections.singletonList("Unknown command. Type \"/help\" for help.");
}
