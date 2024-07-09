package thunder.hack.injection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.modules.Module;
import thunder.hack.modules.misc.ChatUtils;
import thunder.hack.modules.misc.NameProtect;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static thunder.hack.modules.Module.mc;

@Mixin(value = {TextVisitFactory.class})
public class MixinTextVisitFactory {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z" }, index = 0)
    private static String adjustText(String text) {
        return protect(text);
    }

    private static String protect(String string) {
        assert mc.player != null;


        if (!ModuleManager.nameProtect.isEnabled() || mc.player == null) {
            return string;
        }
        String me = mc.getSession().getUsername();
        if (string.contains(me)) {
            return string.replace(me, NameProtect.getCustomName());
        }
        // by dest4590
        List<String> friends = thunder.hack.core.impl.FriendManager.getFriends();
        for (String friend : friends) {
            if (string.contains(friend)) {
                string = string.replace(friend, NameProtect.getCustomFriendsName());
            }

        } return string;
}
}
