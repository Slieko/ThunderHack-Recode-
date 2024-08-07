package thunder.hack.injection;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.impl.CommandManager;
import thunder.hack.core.impl.ProxyManager;
import thunder.hack.gui.misc.DialogScreen;
import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.modules.client.ClientSettings;
import thunder.hack.utility.ClientClickEvent;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static thunder.hack.modules.Module.mc;
import static thunder.hack.modules.client.ClientSettings.isRu;

@Mixin(Screen.class)
public abstract class MixinScreen {
    /*@ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V"))
    private void getList(Args args, MatrixStack matrixStack, ItemStack itemStack, int x, int y) {
        Tooltips tooltips = (Tooltips) Thunderhack.moduleRegistry.get(Tooltips.class);

        if (hasItems(itemStack) && tooltips.storage.getValue() || (itemStack.getItem() == Items.ENDER_CHEST && tooltips.echest.getValue())) {
            //if(args.size() < 3) return;
            //List<Text> lines = args.get(1);

            //int yChanged = y - 4;
            //yChanged -= 10 * lines.size();

            args.set(4, 30);
        }
    }*/

    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 1, remap = false), cancellable = true)
    private void onRunCommand(Style style, CallbackInfoReturnable<Boolean> cir) {
        if (Objects.requireNonNull(style.getClickEvent()) instanceof ClientClickEvent clientClickEvent && clientClickEvent.getValue().startsWith(ThunderHack.commandManager.getPrefix()))
            try {
                CommandManager manager = ThunderHack.commandManager;
                manager.getDispatcher().execute(style.getClickEvent().getValue().substring(ThunderHack.commandManager.getPrefix().length()), manager.getSource());
                cir.setReturnValue(true);
            } catch (CommandSyntaxException ignored) {
            }
    }

    @Inject(method = "filesDragged", at = @At("HEAD"))
    public void filesDragged(List<Path> paths, CallbackInfo ci) {
        String configPath = paths.get(0).toString();
        File cfgFile = new File(configPath);
        String configName = cfgFile.getName();

        DialogScreen dialogScreen = new DialogScreen(isRu() ? "Обнаружен конфиг!" : "Config detected!",
                isRu() ? "Ты действительно хочешь загрузить " + configName + "?" : "Are you sure you want to load " + configName + "?",
                isRu() ? "Да ебать" : "Do it, piece of shit!", isRu() ? "Не, че за хуйня?" : "Nooo fuck ur ass nigga!",
                () -> {
                    ThunderHack.moduleManager.onUnload("none");
                    ThunderHack.configManager.load(cfgFile);
                    ThunderHack.moduleManager.onLoad("none");
                    mc.setScreen(null);
                }, () -> mc.setScreen(null));


        if (mc.currentScreen instanceof WindowsScreen) {
            DialogScreen dialogScreen2 = new DialogScreen(isRu() ? "Обнаружен файл!" : "File detected!",
                    isRu() ? "Импортировать файл " + configName + " как" : "Import file " + configName + " as",
                    isRu() ? "Прокси" : "Proxies", isRu() ? "Конфиг" : "Config",
                    () -> {
                        try {
                            try (BufferedReader reader = new BufferedReader(new FileReader(cfgFile))) {
                                while (reader.ready()) {
                                    String[] line = reader.readLine().split(":");

                                    String ip = line[0];
                                    String port = line[1];
                                    String login = line[2];
                                    String password = line[3];

                                    int p = 80;

                                    try {
                                        p = Integer.parseInt(port);
                                    } catch (Exception e) {
                                        LogUtils.getLogger().warn(e.getMessage());
                                    }

                                    ThunderHack.proxyManager.addProxy(new ProxyManager.ThProxy("Proxy" + (int) MathUtility.random(0, 10000), ip, p, login, password));
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        mc.setScreen(null);
                    },
                    () -> {
                        mc.setScreen(dialogScreen);
                    });
            mc.setScreen(dialogScreen2);
            return;
        }

        if (!configName.contains(".th"))
            return;

        mc.setScreen(dialogScreen);
    }

    @SuppressWarnings("all")
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackgroundHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(ClientSettings.customMainMenu.getValue() && mc.world == null) {
            ci.cancel();
            Render2DEngine.drawMainMenuShader(context.getMatrices(), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        }
    }
}