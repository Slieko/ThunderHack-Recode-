package thunder.hack.modules.Slk;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.modules.Module;


public class AutoCrash extends Module {
    public AutoCrash() {
        super("AutoCrash", Category.SLK);
    }

    @EventHandler
    public void onEnable(PlayerUpdateEvent e) {
        OpenScreenS2CPacket packetOpen = new OpenScreenS2CPacket(2, ScreenHandlerType.GENERIC_9X3, null);
        sendPacket(packetOpen);

        ModuleManager.autoCrash.disable("Interaction Succeful");
    }
}
