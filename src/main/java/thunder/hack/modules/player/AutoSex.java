package thunder.hack.modules.player;

import baritone.api.BaritoneAPI;
import net.minecraft.entity.player.PlayerEntity;
import thunder.hack.ThunderHack;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.modules.Module;
import thunder.hack.modules.movement.AutoWalk;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AutoSex extends Module {
    private final Setting<Integer> targetRange = new Setting<>("Target Range", 5, 1, 10);
    private final Setting<SexMode> mode = new Setting<>("Sex Mode", SexMode.Active);
    private final Setting<Integer> msgDelay = new Setting<>("Message Delay", 1, 0, 50);
    private final Setting<Boolean> autoPath = new Setting<>("AutoPath", false);

    private enum SexMode {
        Active,
        Passive
    }


    private static final String[] PASSIVE_MESSAGES = {
            "It's so Biiiiiiig",
            "Be careful daddy <3",
            "Oh, I feel it inside me!"
    };
    private static final String[] ACTIVE_MESSAGES = {
            "Oh, I'm cumming!",
            "Oh, ur pussy is so nice!",
            "Yeah, yeah",
            "I feel u!",
            "Oh, im inside u",
            "Your dick is shaking so cool",
            "U are so cummy and tasty",
            "Your dick looks like small slut"
    };

    private PlayerEntity target;
    private final Timer messageTimer = new Timer();
    private final Timer sneakTimer = new Timer();

    public AutoSex() {
        super("AutoSex", Category.PLAYER);
    }

    @Override
    public void onUpdate() {

        if (fullNullCheck()) return;
        if (target == null) {
            target = ThunderHack.combatManager.getNearestTarget(targetRange.getValue());
            return;
        }
        if (target.getPos().squaredDistanceTo(mc.player.getPos()) >= targetRange.getPow2Value()) {
            target = null;
            return;
        }

        switch (mode.getValue()) {
            case Active -> {
                if (sneakTimer.passedMs((long) MathUtility.random(200, 1200))) {
                    mc.options.sneakKey.setPressed(!mc.options.sneakKey.isPressed());
                    sneakTimer.reset();
                }
            }
            case Passive -> {
                if (!mc.options.sneakKey.isPressed())
                    mc.options.sneakKey.setPressed(true);
            }
        }
        if(autoPath.getValue()){
            Objects.requireNonNull(mc.getNetworkHandler()).sendChatCommand(BaritoneAPI.getSettings().prefix.value + "goto" + mc.player.getX() +mc.player.getY() + mc.player.getZ());
        }
        if (messageTimer.passedMs(msgDelay.getValue() * 1000) && mc.getNetworkHandler() != null) {
            List<String> messages = Arrays.stream(mode.getValue() == SexMode.Active ? ACTIVE_MESSAGES : PASSIVE_MESSAGES).toList();
            mc.getNetworkHandler().sendChatCommand("msg " + target.getName().getString() + " " +messages.get((int) (Math.random() * messages.size())));
            messageTimer.reset();
        }
        }

    @Override
    public void onDisable() {
        if (autoPath.getValue()) {
            assert mc.player != null;
            mc.player.networkHandler.sendChatMessage(BaritoneAPI.getSettings().prefix.value + "stop");
        }
    }
}

