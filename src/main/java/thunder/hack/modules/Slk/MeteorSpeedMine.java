package thunder.hack.modules.Slk;

import meteordevelopment.orbit.EventHandler;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

import static net.minecraft.entity.effect.StatusEffects.HASTE;


public class MeteorSpeedMine extends Module{
    public MeteorSpeedMine() {
        super("MeteorSpeedMine", Module.Category.SLK);
    }
    public static final Setting<Integer> amplifier = new Setting<>("Effect Level", 1, 1, 5);

  @EventHandler
    public void onEnable(PlayerUpdateEvent e){
      mc.player.setStatusEffect(new StatusEffectInstance(HASTE, -1, amplifier.getValue() -1, false, false, false), null);

  }
    @Override
    public void onDisable(){
        mc.player.removeStatusEffect(StatusEffects.HASTE);
    }
}
