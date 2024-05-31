package thunder.hack.modules.client;


import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

public class QunixNew1 extends Module{
    public QunixNew1() {
        super("QunixNew1", Category.CLIENT);
    }
    public static final Setting<Integer> speed = new Setting<>("Speed", 500, 0, 1000);
    public static final Setting<Float> factor = new Setting<>("Factor", 0.5f, 0f, 10f);
}
