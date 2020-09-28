# Tamer 🦊

### **This project is still WIP!**

Tamer is a plugin for [`PaperMC`](https://github.com/papermc/paper) (1.14 - 1.16.3), which not only allows players to 
protect their entities from other players, but also enhances the vanilla entity taming/breeding mechanics!

# Features
* **Lightweight & fast** entity claiming plugin
* **Easy & innovative** entity taming/breeding mechanics
* **Absolutely no** dependencies  
* **Optional** PlaceholderAPI support

# Installation 

Due to this plugin depending on private APIs, it is currently not possible to compile it from source. This might change in the future.

To install this plugin, just download the latest release from the [`Releases`](https://github.com/kokumaji/Tamer/releases) page and 
put it into your server's plugins folder. There are no additional steps required.

# Usage & Permissions 

| Command | Permission | Description |
| --- | --- | --- | 
| /tamer | tamer.command | Main command
| /tamer info | tamer.command.info | Get information about the entity you are currently looking at.
| /tamer claim | tamer.command.claim | Claim the current entity you are looking at.
| /tamer unclaim | tamer.command.claim | Abandon the current entity you are looking at.
| /tamer allow [Player] | tamer.command.allow | Allow another player to interact with your entity.
| /tamer deny [Player] | tamer.command.allow | Remove a 'whitelisted' player.
| /tamer list | tamer.command.list | List all allowed players.
| /tamer book | tamer.command.book | Get the entity claiming tool.

| Permission | Description | 
| --- | --- |
| tamer.bypass | Bypass entity protection 

### **How to use the claim tool** 
In order to use the claim tool, a player requires the permission `tamer.command.book` as well as the `tamer.command.allow` permission to add new players.
To claim an entity, simply `Shift + Rightclick` on an compatible entity (as of right now, only animals may be protected)!

### **Configuration** 

Due to this being a beta/early release, there are no configuration options except for the plugin prefix. More options will be added as this project grows. 

# Planned Features

* **Add support** for 1.8 - 1.13
* **Custom** entity attributes & 'spells'

