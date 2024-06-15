package thunder.hack.modules.client;

import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.gui.windows.impl.*;
import thunder.hack.modules.Module;

public class Windows extends Module {
    public Windows() {
        super("Windows", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.setScreen(new WindowsScreen(
                MacroWindow.get(),
                ConfigWindow.get(),
                FriendsWindow.get(),
                WaypointWindow.get()
        ));
        disable();
    }
}