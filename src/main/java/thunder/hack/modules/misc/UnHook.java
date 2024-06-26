package thunder.hack.modules.misc;

import baritone.api.utils.SettingsUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.util.Icons;
import net.minecraft.util.Formatting;
import thunder.hack.ThunderHack;
import thunder.hack.modules.Module;
import thunder.hack.modules.client.ClientSettings;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static thunder.hack.modules.client.ClientSettings.isRu;

public class UnHook extends Module {

    // Йо фабос, засунь в о4ко себе фалос
    public UnHook() {
        super("UnHook", Category.MISC);
    }

    private final Setting<Boolean> deleteGz = new Setting<>("Delete .log.gz", false);
    private final Setting<Boolean> reiLog = new Setting<>("Delete rei.log", false);

    List<Module> list;

    public int code = 0;

    @Override
    public void onEnable() {
        code = (int) MathUtility.random(10, 99);
        for (int i = 0; i < 20; i++)
            sendMessage(isRu() ? Formatting.RED + "Ща все свернется, напиши в чат " + Formatting.WHITE + code + Formatting.RED + " чтобы все вернуть!"
                    : Formatting.RED + "It's all close now, write to the chat " + Formatting.WHITE + code + Formatting.RED + " to return everything!");

        list = ThunderHack.moduleManager.getEnabledModules();

        mc.setScreen(null);

        ThunderHack.asyncManager.run(() -> {
            mc.executeSync(() -> {
                for (Module module : list) {
                    if (module.equals(this))
                        continue;
                    module.disable();
                }
                ClientSettings.customMainMenu.setValue(false);

                // Clean icon
                try {
                    mc.getWindow().setIcon(mc.getDefaultResourcePack(), SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
                } catch (Exception e) {
                }

                // Clean chat
                mc.inGameHud.getChatHud().clear(true);
                setEnabled(true);

                // Clean yyyy-mm-dd-h.log.gz
                String directoryPath = mc.runDirectory + File.separator + "logs" + File.separator;
                File directory = new File(directoryPath);

                if (!directory.exists() || !directory.isDirectory()) {
                    return;
                }
               if(deleteGz.getValue()) {
                   String regex = "\\d{4}-\\d{2}-\\d{2}-\\d{1}.log.gz";
                   Pattern pattern = Pattern.compile(regex);

                   File[] filesToDelete = directory.listFiles((dir, name) -> pattern.matcher(name).matches());

                   if (filesToDelete == null || filesToDelete.length == 0) {
                       System.out.println("No logs to remove!");
                   } else {
                       for (File file : filesToDelete) {
                           try {
                               Files.delete(Paths.get(file.getAbsolutePath()));
                               if(reiLog.getValue())
                                Files.delete(Paths.get(mc.runDirectory + File.separator + "logs" + File.separator + "rei.log"));
                           } catch (Exception ignored) {
                           }
                       }
                   }
               }
                //Clean latest.log
                try {
                        File file = new File(mc.runDirectory + File.separator + "logs" + File.separator + "latest.log");
                        FileInputStream fis = new FileInputStream(file);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
                        ArrayList<String> lines = new ArrayList<>();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("thunderhack") || line.contains("ThunderHack") || line.contains("$$") || line.contains("\\______/")
                                    || line.contains("By Pan4ur & 06ED") || line.contains("�f?�6?�f?�r") ||line.contains("baritone") || line.contains("Forked by sl1eko") || line.contains("th-visuals") || line.contains("thunder.hack") || line.contains("[STDOUT]") || line.contains("satin") || line.contains("[�5UnHook�7]") || line.contains("[�5JumpCircle�7]")|| line.contains("[�5TargetHud�7]")|| line.contains("[�5WayPoints�7]") || line.contains("\u26A1"))
                                continue;
                            lines.add(line);
                        }
                        fis.close();
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                            for (String s : lines)
                                writer.write(s + "\n");
                        } catch (Exception ignored) {
                        }
                    } catch (IOException ignored) {
                    }
            });
        }, 5000);
    }


    @Override
    public void onDisable() {
        if (list == null)
            return;

        for (Module module : list) {
            if (module.equals(this))
                continue;
            module.enable();
        }
        ClientSettings.customMainMenu.setValue(true);

    }
}