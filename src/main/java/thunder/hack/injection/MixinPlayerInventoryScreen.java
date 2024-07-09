package thunder.hack.injection;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.ThunderHack;

import static thunder.hack.ThunderHack.mc;

@Mixin(InventoryScreen.class)
public abstract class MixinPlayerInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    public MixinPlayerInventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    private void dropItems(){
        for (int i = 0; i < 45 && mc.currentScreen !=null; ++i) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickSlot(0, i, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, -999, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    protected void init(CallbackInfo ci){
        if(!ThunderHack.unhooked) {
            super.init();
            addDrawableChild(new ButtonWidget.Builder(Text.literal("Выбросить всё"), button -> dropItems())
                    .position(width / 2 - 50, height / 2 - 110)
                    .size(100, 20)
                    .build()
            );
        }
    }
}
