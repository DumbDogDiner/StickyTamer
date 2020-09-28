package me.kokumaji.Tamer.Listeners;

import me.kokumaji.HibiscusAPI.api.translation.Translator;
import me.kokumaji.Tamer.Objects.EditorCache;
import me.kokumaji.Tamer.Tamer;
import me.kokumaji.Tamer.Util.ClaimingUtil;
import me.kokumaji.Tamer.Util.Messages;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(EditorCache.GetEntity(p) != null) {
            Translator tr = Tamer.GetTranslator();
            Entity ent = EditorCache.GetEntity(p);
            String msg = e.getMessage();

            e.setCancelled(true);

            if(msg.equalsIgnoreCase("!cancel")) {
                EditorCache.RemovePlayer(p);
                Messages.Send(p, tr.Translate("entity.player-add-cancelled", true));
                return;
            }

            if(msg.split(" ").length > 1) {
                Messages.Send(p, tr.Translate("entity.player-add-invalid", true));
                return;
            }

            ClaimingUtil.AddUser(p, ent, msg);
            EditorCache.RemovePlayer(p);
        }
    }
}
