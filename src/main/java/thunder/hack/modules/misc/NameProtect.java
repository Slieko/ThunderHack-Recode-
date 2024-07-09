package thunder.hack.modules.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardObjective;
import thunder.hack.ThunderHack;
import thunder.hack.core.impl.FriendManager;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.gui.hud.impl.TargetHud;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

import javax.naming.Name;
import java.util.Objects;
import java.util.Set;

public class NameProtect extends Module {
    public NameProtect() {
        super("NameProtect", Category.MISC);
    }

    public static Setting<String> newName = new Setting<>("name", "Hell_Raider");
    // friends name protect by dest4590 (https://github.com/dest4590/ThunderHack-Recode/commit/c77e0b9a71afcaec41d88793f12e9cb51328f051#diff-34f70393f6b4a9eebb6ef8909815d48f52849a9860bb19fe7fe1203d68f663a0)
    public static Setting<Boolean> friendProtect = new Setting<>("Friend Protect", Boolean.TRUE);
    public static Setting<String> friendName = new Setting<>("Friend Name", "Hell_Raider").addToGroup(friendProtect);

    public static String getCustomName() {
        return ModuleManager.nameProtect.isEnabled() ? newName.getValue().replaceAll("&", "\u00a7") : mc.getGameProfile().getName();
    }
    public static String getCustomFriendsName() {
        return ModuleManager.nameProtect.isEnabled() && NameProtect.friendProtect.getValue() ? friendName.getValue().replaceAll("&", "\u00a7") : mc.getName();
    }



}