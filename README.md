# BlockerVelocity

Lightweight command/tab complete blocker.  
Avoid players seeing/executing commands that you don't want them to see.

This plugin was originally created for the [PotPvP](https://www.potpvp.cc/) network.  
Other plugins of the same type either force dependencies on other library plugins. Have redundant features or the configuration is too complex.  

Later I decided to open source it. Because I don't want others to be in the same predicament for that reason.

## Features

  - Listening on packet-level to prevent trigger events.
  - Don't have redundant features.
  - No need to create dependencies with other plugins. 
(However, you'll need [LuckPerms](https://luckperms.net) to get a better manage of permissions.)
  - Lightweight will also show that it is fast.

## Configuration

The default group will block some anti-cheat plugins and bukkit commands.  
If you don't have more plugins to hide. 
You don't need to change the configuration of default blocked command list.

To hide it completely. 
You'll also need to adjust the `blocked-tip` to the same message as when you enter an unknown command.  

The default message is `"Unknown command. Type "/help" for help."`.  
If this is the message that your server is displaying an unknown command. You don't need to change it.  
If the returned message states `"Unknown or incomplete command"`. 
The following message is configured to achieve the same effect:

```hocon
blocked-tip=[
  "<red><lang:command.unknown.command></red>",
  "<red><u><command></u><i><lang:command.context.here></i></red>"
]
```

By default, BlockerVelocity doesn't apply any groups for players.  
What group is applied to a player depends on the permissions the player has. (`blocker.group.<group>`)

To bypass the group. They should not be assigned that permission. 
Or add `blocker.bypass.<group>` permissions. (Also applies if you have  `*` permission.)

If you have `blocker.command` permissions. 
You can then use the `/blockervelocity` command to reload the configuration file.

Wait for you to configure everything. Then enjoy it.

## Special thanks

  - [Jones](https://github.com/jonesdevelopment)'s [repository](https://repo.jonesdev.xyz/releases/) for exposed velocity proxy classes.
  - [Velocity](https://github.com/PaperMC/Velocity) made by [PaperMC](https://github.com/PaperMC)