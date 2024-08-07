package thunder.hack.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import ru.vidtu.ias.gui.IASConfigScreen;
import the_fireplace.ias.gui.AccountListScreen;
import thunder.hack.ThunderHack;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.ThunderUtility;
import thunder.hack.utility.render.Render2DEngine;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static thunder.hack.modules.Module.mc;

public class MainMenuScreen extends Screen {
    private static final Identifier TH_TEAM = new Identifier("thunderhack","textures/gui/elements/thteam.png");
    private static final Identifier FORK = new Identifier("thunderhack","textures/gui/elements/slk.png");
    private static final Identifier IAS = new Identifier("thunderhack","textures/gui/elements/friendmanagericon.png");
    private static final Identifier DONATION = new Identifier("thunderhack", "textures/gui/elements/donationalerts.png");
    Screen prevScreen = mc.currentScreen;

    private final List<MainMenuButton> buttons = new ArrayList<>();
    public boolean confirm = false;
    public static int ticksActive;

    protected MainMenuScreen() {
        super(Text.of("THMainMenuScreen"));
        INSTANCE = this;

        buttons.add(new MainMenuButton(-110, -70, I18n.translate("menu.singleplayer").toUpperCase(Locale.ROOT), () -> mc.setScreen(new SelectWorldScreen(this))));
        buttons.add(new MainMenuButton(4, -70, I18n.translate("menu.multiplayer").toUpperCase(Locale.ROOT), () -> mc.setScreen(new MultiplayerScreen(this))));
        buttons.add(new MainMenuButton(-110, -29, I18n.translate("menu.options")
                .toUpperCase(Locale.ROOT)
                .replace(".", ""), () -> mc.setScreen(new OptionsScreen(this, mc.options))));
        buttons.add(new MainMenuButton(4, -29, "CLICKGUI", () -> ModuleManager.clickGui.setGui()));
        buttons.add(new MainMenuButton(-110, 12, I18n.translate("menu.quit").toUpperCase(Locale.ROOT), mc::scheduleStop, true));
    }

    private static MainMenuScreen INSTANCE = new MainMenuScreen();

    public static MainMenuScreen getInstance() {
        ticksActive = 0;

        if (INSTANCE == null) {
            INSTANCE = new MainMenuScreen();
        }
        return INSTANCE;
    }

    @Override
    public void tick() {
        ticksActive++;

        if (ticksActive > 400) {
            ticksActive = 0;
        }
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;

        float mainX = halfOfWidth - 120f;
        float mainY = halfOfHeight - 80f;
        float mainWidth = 240f;
        float mainHeight = 140;


        renderBackground(context, mouseX, mouseY, delta);

        Render2DEngine.drawHudBase(context.getMatrices(), mainX, mainY, mainWidth, mainHeight, 20);

        buttons.forEach(b -> b.onRender(context, mouseX, mouseY));

        Render2DEngine.drawHudBase(context.getMatrices(), mc.getWindow().getScaledWidth() - 60, mc.getWindow().getScaledHeight() - 60, 40, 40, 5, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 60, mc.getWindow().getScaledHeight() - 60, 40, 40) ? 0.7f : 1f);
        RenderSystem.setShaderColor(1f, 1f, 1f, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 60, mc.getWindow().getScaledHeight() - 40, 40, 40) ? 0.7f : 1f);
        context.drawTexture(TH_TEAM, mc.getWindow().getScaledWidth() - 60, mc.getWindow().getScaledHeight() - 60, 40, 40, 0, 0, 40, 40, 40, 40);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Render2DEngine.drawHudBase(context.getMatrices(), mc.getWindow().getScaledWidth() - 120, mc.getWindow().getScaledHeight() - 60, 40, 40, 5, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 120, mc.getWindow().getScaledHeight() - 60, 40, 40) ? 0.7f : 1f);
        RenderSystem.setShaderColor(1f, 1f, 1f, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 120, mc.getWindow().getScaledHeight() - 40, 40, 40) ? 0.7f : 1f);
        context.drawTexture(DONATION, mc.getWindow().getScaledWidth() - 120, mc.getWindow().getScaledHeight() - 60, 40, 40, 0, 0, 40, 40, 40, 40);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Render2DEngine.drawHudBase(context.getMatrices(), mc.getWindow().getScaledWidth() - 180, mc.getWindow().getScaledHeight() - 60, 40, 40, 5, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 180, mc.getWindow().getScaledHeight() - 60, 40, 40) ? 0.7f : 1f);
        RenderSystem.setShaderColor(1f, 1f, 1f, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 120, mc.getWindow().getScaledHeight() - 40, 40, 40) ? 0.7f : 1f);
        context.drawTexture(FORK, mc.getWindow().getScaledWidth() - 180, mc.getWindow().getScaledHeight() - 60, 40, 40, 0, 0, 40, 40, 40, 40);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if(ThunderHack.ias) {
            Render2DEngine.drawHudBase(context.getMatrices(),mc.getWindow().getScaledWidth() /2+125, mc.getWindow().getScaledHeight()/ 4 + 146, 40, 40, 5, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() /2+125, mc.getWindow().getScaledHeight()/ 4 + 146, 40, 40) ? 0.7f : 1f);
            RenderSystem.setShaderColor(1f, 1f, 1f, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() /2+125, mc.getWindow().getScaledHeight()/ 4 + 146, 40, 40) ? 0.7f : 1f);
            context.drawTexture(IAS, mc.getWindow().getScaledWidth() /2+125, mc.getWindow().getScaledHeight()/ 4 + 148, 40, 40, 0, 0, 40, 40, 40, 40);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        boolean hoveredLogo = Render2DEngine.isHovered(mouseX, mouseY, (int) (halfOfWidth - 120), (int) (halfOfHeight - 130), 210, 50);

        FontRenderers.thglitchBig.drawCenteredString(context.getMatrices(), "THUNDERHACK", (int) (halfOfWidth), (int) (halfOfHeight - 120), new Color(255, 255, 255, hoveredLogo ? 230 : 180).getRGB());

        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY, halfOfWidth - 50, halfOfHeight + 70, 100, 10);

        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "<-- Back to default menu", halfOfWidth, halfOfHeight + 70, hovered ? -1 : Render2DEngine.applyOpacity(-1, 0.6f));
        //  FontRenderers.sf_medium.drawString(context.getMatrices(), "By Pan4ur & 06ED", halfOfWidth * 2 - FontRenderers.sf_medium.getStringWidth("By Pan4ur & 06ED") - 5f, halfOfHeight * 2 - 10, Render2DEngine.applyOpacity(-1, 0.4f));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;
        buttons.forEach(b -> b.onClick((int) mouseX, (int) mouseY));

        if (Render2DEngine.isHovered(mouseX, mouseY, halfOfWidth - 50, halfOfHeight + 70, 100, 10)) {
            confirm = true;
            mc.setScreen(new TitleScreen());
            confirm = false;
        }

        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 60, mc.getWindow().getScaledHeight() - 60, 40, 40))
            mc.setScreen(CreditsScreen.getInstance());
        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 120, mc.getWindow().getScaledHeight() - 60, 40, 40))
            Util.getOperatingSystem().open(URI.create("https://www.donationalerts.com/r/06ed/"));
        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getScaledWidth() - 180, mc.getWindow().getScaledHeight() - 60, 40, 40))
            Util.getOperatingSystem().open(URI.create("https://github.com/Slieko/ThunderHack-Recode-"));

        if (ThunderHack.ias && Render2DEngine.isHovered(mouseX, mouseY, 605, 267, 40, 40))
            mc.setScreen(new AccountListScreen(prevScreen));
        if (Render2DEngine.isHovered(mouseX, mouseY, (int) (halfOfWidth - 157), (int) (halfOfHeight - 140), 300, 70))
            Util.getOperatingSystem().open(URI.create("https://thunderhack-site.vercel.app/"));


        return super.mouseClicked(mouseX, mouseY, button);
    }
}
