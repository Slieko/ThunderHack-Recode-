package thunder.hack.gui.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import thunder.hack.ThunderHack;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudElement;
import thunder.hack.modules.client.HudEditor;
import thunder.hack.modules.client.Media;
import thunder.hack.modules.misc.NameProtect;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextUtil;

import java.awt.*;
import java.util.Set;

import static thunder.hack.core.impl.ServerManager.getPing;

public class WaterMark extends HudElement {
    public WaterMark() {
        super("WaterMark", 256, 36);
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.Big);
    private final Setting<Boolean> ru = new Setting<>("RU", false, v->HudEditor.hudStyle.getValue() == HudEditor.HudStyle.Glowing && mode.getValue() != Mode.Small);
    private final Setting<Boolean> name_i = new Setting<>("RenderName", true,v-> mode.getValue() == Mode.Small);
    private final Setting<Boolean> server_ip = new Setting<>("RenderServer", true,v-> mode.getValue() == Mode.Small);
    private final Setting<Boolean> customName = new Setting<>("CustomName", false,v-> mode.getValue() == Mode.Small);
    public final Setting<String> name = new Setting<>("nickname", "notnull", v->customName.getValue() && mode.getValue() == Mode.Small);

    public static Identifier logo = new Identifier("thunderhack", "textures/hud/icons/mini_logo.png");
    private final Identifier player = new Identifier("thunderhack", "textures/gui/headers/player.png");
    private final Identifier server = new Identifier("thunderhack", "textures/hud/icons/server.png");
    private final Identifier baltika = new Identifier("thunderhack", "textures/hud/icons/baltika9.png");


    private final TextUtil textUtil = new TextUtil(
            "ТандерХак",
            "ГромХак",
            "ГрозаКлиент",
            "ТандерХуй",
            "ТандерХряк",
            "ТандерХрюк",
            "ТиндерХак",
            "ТундраХак",
            "ГромВзлом"
    );


    private enum Mode {
        Big, Small, Classic, BaltikaClient
    }

    public void onRender2D(DrawContext context) {
        super.onRender2D(context);

        String username = ((ModuleManager.media.isEnabled() && Media.nickProtect.getValue()) || (ModuleManager.nameProtect.isEnabled())) ? (ModuleManager.nameProtect.isEnabled() ? NameProtect.getCustomName() : "Protected") : mc.getSession().getUsername();
            if (mode.getValue() == Mode.Big) {
                Render2DEngine.drawHudBase(context.getMatrices(), getPosX(), getPosY(), 106, 30, HudEditor.hudRound.getValue());
                FontRenderers.thglitch.drawString(context.getMatrices(), "THUNDERHACK", getPosX() + 5.5, getPosY() + 5, -1);
                FontRenderers.monsterrat.drawGradientString(context.getMatrices(), "recode", getPosX() + 35.5f, getPosY() + 21f, 1);
                setBounds(getPosX(), getPosY(), 150, 30);
            }
            else if (mode.getValue() == Mode.Small) {
                if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                    float offset1 = FontRenderers.sf_bold.getStringWidth(username) + 72;
                    if (customName.getValue())
                        offset1 = FontRenderers.sf_bold.getStringWidth(name.getValue()) + 70;
                    float offset2 = FontRenderers.sf_bold.getStringWidth((mc.isInSingleplayer() ? "SinglePlayer" : mc.getNetworkHandler().getServerInfo().address));

                    Render2DEngine.drawRoundedBlur(context.getMatrices(), getPosX(), getPosY(), 50f, 15f, 3, HudEditor.blurColor.getValue().getColorObject());
                    if (name_i.getValue() && !customName.getValue())
                        Render2DEngine.drawRoundedBlur(context.getMatrices(), getPosX() + 55, getPosY(), offset1 - 55, 15f, 3, HudEditor.blurColor.getValue().getColorObject());
                    if (name_i.getValue() && customName.getValue())
                        Render2DEngine.drawRoundedBlur(context.getMatrices(), getPosX() + 55, getPosY(), offset1 + name.getValue().length() - 60, 15f, 3, HudEditor.blurColor.getValue().getColorObject());
                    if (server_ip.getValue() && !customName.getValue())
                        Render2DEngine.drawRoundedBlur(context.getMatrices(), getPosX() + offset1 + 5.5f, getPosY(), offset2 + 20, 15f, 3, HudEditor.blurColor.getValue().getColorObject());
                    if (server_ip.getValue() && customName.getValue())
                        Render2DEngine.drawRoundedBlur(context.getMatrices(), getPosX() +  name.getValue().length() + offset1 + 1f, getPosY(), offset2 + 23, 15f, 3, HudEditor.blurColor.getValue().getColorObject());

                    Render2DEngine.setupRender();

                    Render2DEngine.drawLine(getPosX() + 14, getPosY() - 0.5f,getPosX() + 14, getPosY() + 15f, HudEditor.getColor(270).getRGB());

                    FontRenderers.sf_bold.drawGradientString(context.getMatrices(), "Recode", getPosX() + 18, getPosY() + 4.5f, 20);
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                    RenderSystem.setShaderTexture(0, logo);
                    Render2DEngine.renderGradientTexture(context.getMatrices(), getPosX() + 1.5f, getPosY() + 1.5f, 11, 11, 0, 0, 128, 128, 128, 128,
                            HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));

                    if (name_i.getValue()) {
                        RenderSystem.setShaderTexture(0, player);
                        Render2DEngine.renderGradientTexture(context.getMatrices(), getPosX() + 58, getPosY() + 3, 8, 8, 0, 0, 128, 128, 128, 128,
                                HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
                    }
                    if (server_ip.getValue() && customName.getValue()) {
                        RenderSystem.setShaderTexture(0, server);
                        Render2DEngine.renderGradientTexture(context.getMatrices(), getPosX() + offset1 + name.getValue().length() + 5f, getPosY() + 2, 10, 10, 0, 0, 128, 128, 128, 128,
                                HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
                    }
                    if (server_ip.getValue() && !customName.getValue()) {
                        RenderSystem.setShaderTexture(0, server);
                        Render2DEngine.renderGradientTexture(context.getMatrices(), getPosX() + offset1 + 8f, getPosY() + 2, 10, 10, 0, 0, 128, 128, 128, 128,
                                HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
                    }
                    Render2DEngine.endRender();

                    Render2DEngine.setupRender();
                    RenderSystem.defaultBlendFunc();
                    if (name_i.getValue() && !customName.getValue()) {
                        FontRenderers.sf_bold.drawString(context.getMatrices(), username, getPosX() + 68, getPosY() + 4.5f, HudEditor.textColor.getValue().getColor());
                    } else if (name_i.getValue() && customName.getValue()){
                        FontRenderers.sf_bold.drawString(context.getMatrices(), name.getValue(), getPosX() + 68, getPosY() + 4.5f, HudEditor.textColor.getValue().getColor());
                    }
                    if(server_ip.getValue() && !customName.getValue()) {
                        FontRenderers.sf_bold.drawString(context.getMatrices(), (mc.isInSingleplayer() ? "SinglePlayer" : mc.getNetworkHandler().getServerInfo().address), getPosX() + offset1 + 21.5f, getPosY() + 4.5f, HudEditor.textColor.getValue().getColor());
                    }
                    if(server_ip.getValue() && customName.getValue()) {
                        FontRenderers.sf_bold.drawString(context.getMatrices(), (mc.isInSingleplayer() ? "SinglePlayer" : mc.getNetworkHandler().getServerInfo().address), getPosX() + offset1 + name.getValue().length()+ 18.5f, getPosY() + 4.5f, HudEditor.textColor.getValue().getColor());
                    }
                    Render2DEngine.endRender();
                    setBounds(getPosX(), getPosY(), 100, 15f);
                } else {
                    String info = Formatting.DARK_GRAY + "| " + Formatting.RESET + username + Formatting.DARK_GRAY + " | " + Formatting.RESET + getPing() + " ms" + Formatting.DARK_GRAY + " | " + Formatting.RESET + (mc.isInSingleplayer() ? "SinglePlayer" : mc.getNetworkHandler().getServerInfo().address);
                    float width = FontRenderers.sf_bold.getStringWidth("ThunderHack " + info) + 5;
                    Render2DEngine.drawHudBase(context.getMatrices(), getPosX(), getPosY(), width, 10, 3);
                    FontRenderers.sf_bold.drawGradientString(context.getMatrices(), ru.getValue() ? textUtil + " " : "ThunderHack ", getPosX() + 2, getPosY() + 2.5f, 10);
                    FontRenderers.sf_bold.drawString(context.getMatrices(), info, getPosX() + 2 + FontRenderers.sf_bold.getStringWidth("ThunderHack "), getPosY() + 2.5f, HudEditor.textColor.getValue().getColor());
                    setBounds(getPosX(), getPosY(), width, 10);
                }

            }else if (mode.getValue() == Mode.BaltikaClient) {
                Render2DEngine.drawHudBase(context.getMatrices(), getPosX(), getPosY(), 100, 64, HudEditor.hudRound.getValue());
                context.drawTexture(baltika,  (int)getPosX(), (int)getPosY() + 2, 1, 1, 64, 64, 65, 64);
                FontRenderers.thglitch.drawString(context.getMatrices(), "    BALTIKA", getPosX() + 23, getPosY() + 41.5, -1);
                setBounds(getPosX(), getPosY(), 106, 30);
            } else {
                FontRenderers.monsterrat.drawGradientString(context.getMatrices(), "ThunderHack v" + ThunderHack.VERSION, getPosX() + 5.5f, getPosY() + 5, 10);
                setBounds(getPosX(), getPosY(), 100, 3);
            }
        }
    @Override
    public void onUpdate () {
        textUtil.tick();
    }
}

