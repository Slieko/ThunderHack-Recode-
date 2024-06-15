package thunder.hack.core.impl;

import com.google.gson.*;
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.cmd.Command;
import thunder.hack.cmd.impl.NukerCommand;
import thunder.hack.core.IManager;
import thunder.hack.modules.Module;
import thunder.hack.modules.misc.Nuker;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static thunder.hack.modules.client.ClientSettings.isRu;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigManager implements IManager {
    public static final String CONFIG_FOLDER_NAME = "lambdynlights";
    public static final File MAIN_FOLDER = new File(mc.runDirectory+"/config", CONFIG_FOLDER_NAME);
    public static final File CONFIGS_FOLDER = new File(MAIN_FOLDER, "configs");
    public static final File TEMP_FOLDER = new File(MAIN_FOLDER, "temp");
    public static final File MISC_FOLDER = new File(MAIN_FOLDER, "misc");
    public static final File SOUNDS_FOLDER = new File(MISC_FOLDER, "sounds");
    public static final File IMAGES_FOLDER = new File(MISC_FOLDER, "images");
    public static final File TABPARSER_FOLDER = new File(MISC_FOLDER, "tabparser");
    public static final File STASHLOGGER_FOLDER = new File(MISC_FOLDER, "stashlogger");


    public File currentConfig = null;

    public static boolean firstLaunch = false;

    public ConfigManager() {
        if (!MAIN_FOLDER.exists()) {
            MAIN_FOLDER.mkdirs();
            firstLaunch = true;
        }
        if (!CONFIGS_FOLDER.exists()) CONFIGS_FOLDER.mkdirs();
        if (!TEMP_FOLDER.exists()) TEMP_FOLDER.mkdirs();
        if (!MISC_FOLDER.exists()) MISC_FOLDER.mkdirs();
        if (!SOUNDS_FOLDER.exists()) SOUNDS_FOLDER.mkdirs();
        if (!IMAGES_FOLDER.exists()) IMAGES_FOLDER.mkdirs();
        if (!TABPARSER_FOLDER.exists()) TABPARSER_FOLDER.mkdirs();
        if (!STASHLOGGER_FOLDER.exists()) STASHLOGGER_FOLDER.mkdirs();
    }

    public static @NotNull String getConfigDate(String name) {
        File file = new File(CONFIGS_FOLDER, name + ".th");
        if (!file.exists()) {
            return "none";
        }
        long x = file.lastModified();
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date sol = new Date(x);
        return obj.format(sol);
    }

    public void load(String name, String category) {
        File file = new File(CONFIGS_FOLDER, name + ".th");
        if (!file.exists()) {
            if (isRu()) Command.sendMessage("Конфига " + name + " не существует!");
            else Command.sendMessage("Config " + name + " does not exist!");

            return;
        }

        if (currentConfig != null)
            save(currentConfig);

        ThunderHack.moduleManager.onUnload();
        ThunderHack.moduleManager.onUnloadPost();
        load(file, category);
        ThunderHack.moduleManager.onLoad();
    }

    public void load(String name) {
        File file = new File(CONFIGS_FOLDER, name + ".th");
        if (!file.exists()) {
            if (isRu()) Command.sendMessage("Конфига " + name + " не существует!");
            else Command.sendMessage("Config " + name + " does not exist!");

            return;
        }

        if (currentConfig != null)
            save(currentConfig);

        ThunderHack.moduleManager.onUnload();
        ThunderHack.moduleManager.onUnloadPost();
        load(file);
        ThunderHack.moduleManager.onLoad();
    }

    public void loadCloud(String name) {
        Command.sendMessage(isRu() ? "Загружаю.." : "Downloading..");
        try (BufferedInputStream in = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Pan4ur/THRecodeUtil/main/configs/" + name + ".th").openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(new File(CONFIGS_FOLDER, name + ".th"))) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            Command.sendMessage(isRu() ? "Загрузил!" : "Downloaded!");
            load(name);
        } catch (Exception e) {
            Command.sendMessage(isRu() ? "Произошла ошибка при загрузке! Может название неправильное?" : "There was an error downloading! Maybe the name is wrong?");
        }
    }

    public void loadModuleOnly(String name, Module module) {
        File file = new File(CONFIGS_FOLDER, name + ".th");
        if (!file.exists()) {
            if (isRu()) Command.sendMessage("Конфига " + name + " не существует!");
            else Command.sendMessage("Config " + name + " does not exist!");

            return;
        }

        ThunderHack.moduleManager.onUnload();
        ThunderHack.moduleManager.onUnloadPost();
        loadModuleOnly(file, module);
        ThunderHack.moduleManager.onLoad();
    }

    public void load(@NotNull File config) {
        if (!config.exists()) save(config);
        try {
            FileReader reader = new FileReader(config, StandardCharsets.UTF_8);
            JsonParser parser = new JsonParser();

            JsonArray array = null;
            try {
                array = (JsonArray) parser.parse(reader);
            } catch (ClassCastException e) {
                save(config);
            }

            JsonArray modules = null;
            try {
                JsonObject modulesObject = (JsonObject) array.get(0);
                modules = modulesObject.getAsJsonArray("Modules");
            } catch (Exception ignored) {
            }
            if (modules != null) {
                modules.forEach(m -> {
                    try {
                        parseModule(m.getAsJsonObject(), "none");
                    } catch (NullPointerException e) {
                        System.err.println(e.getMessage());
                    }
                });
            }

            if (isRu()) Command.sendMessage("Загружен конфиг " + config.getName());
            else Command.sendMessage("Loaded " + config.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentConfig = config;
        saveCurrentConfig();
    }

    public void load(@NotNull File config, String category) {
        if (!config.exists()) save(config);
        try {
            FileReader reader = new FileReader(config, StandardCharsets.UTF_8);
            JsonParser parser = new JsonParser();

            JsonArray array = null;
            try {
                array = (JsonArray) parser.parse(reader);
            } catch (ClassCastException e) {
                save(config);
            }

            JsonArray modules = null;
            try {
                JsonObject modulesObject = (JsonObject) array.get(0);
                modules = modulesObject.getAsJsonArray("Modules");
            } catch (Exception ignored) {
            }
            if (modules != null) {
                modules.forEach(m -> {
                    try {
                        parseModule(m.getAsJsonObject(), category);
                    } catch (NullPointerException e) {
                        System.err.println(e.getMessage());
                    }
                });
            }

            if (isRu()) Command.sendMessage("Загружен конфиг " + config.getName());
            else Command.sendMessage("Loaded " + config.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentConfig = config;
        saveCurrentConfig();
    }

    public void loadModuleOnly(File config, Module module) {
        try {
            FileReader reader = new FileReader(config);
            JsonParser parser = new JsonParser();

            JsonArray array = null;
            try {
                array = (JsonArray) parser.parse(reader);
            } catch (ClassCastException ignored) {
            }

            JsonArray modules = null;
            try {
                JsonObject modulesObject = (JsonObject) Objects.requireNonNull(array).get(0);
                modules = modulesObject.getAsJsonArray("Modules");
            } catch (Exception ignored) {
            }

            if (modules != null) {
                modules.forEach(m -> {
                    Module module1 = ThunderHack.moduleManager.modules.stream()
                            .filter(m1 -> m.getAsJsonObject().getAsJsonObject(m1.getName()) != null)
                            .findFirst().orElse(null);

                    if (module1 == null)
                        return;

                    if (Objects.equals(module.getName(), module1.getName())) {
                        try {
                            parseModule(m.getAsJsonObject(), "none");
                        } catch (NullPointerException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                });
            }
            if (isRu()) Command.sendMessage("Загружен модуль " + module.getName() + " с конфига " + config.getName());
            else Command.sendMessage("Loaded " + module.getName() + " from " + config.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String name) {
        File file = new File(CONFIGS_FOLDER, name + ".th");
        if (file.exists()) {
            Command.sendMessage(isRu() ? "Перезаписываем " + name + "..." : "Overwriting " + name + "...");
            file.delete();
        } else {
            Command.sendMessage(isRu() ? "Конфиг " + name + " успешно сохранен!" : "Config " + name + " successfully saved!");
        }
        save(file);
    }

    public void save(@NotNull File config) {
        try {
            if (!config.exists()) {
                config.createNewFile();
            }
            JsonArray array = new JsonArray();

            JsonObject modulesObj = new JsonObject();
            modulesObj.add("Modules", getModuleArray());
            array.add(modulesObj);

            FileWriter writer = new FileWriter(config, StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            gson.toJson(array, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseModule(JsonObject object, String category) throws NullPointerException {
        Module module = ThunderHack.moduleManager.modules.stream()
                .filter(m -> object.getAsJsonObject(m.getName()) != null)
                .findFirst()
                .orElse(null);

        if (!Objects.equals(category, "none") && !module.getCategory().getName().toLowerCase().equals(category))
            return;

        if (module != null) {
            JsonObject mobject = object.getAsJsonObject(module.getName());

            for (Setting setting : module.getSettings()) {
                try {
                    if (setting.getValue() instanceof SettingGroup) {

                    } else if (setting.getValue() instanceof Boolean) {
                        setting.setValue(mobject.getAsJsonPrimitive(setting.getName()).getAsBoolean());
                    } else if (setting.getValue() instanceof Float) {
                        setting.setValue(mobject.getAsJsonPrimitive(setting.getName()).getAsFloat());
                    } else if (setting.getValue() instanceof Integer) {
                        setting.setValue(mobject.getAsJsonPrimitive(setting.getName()).getAsInt());
                    } else if (setting.getValue() instanceof String) {
                        setting.setValue(mobject.getAsJsonPrimitive(setting.getName()).getAsString().replace("%%", " ").replace("++", "/"));
                    } else if (setting.getValue() instanceof Bind) {
                        JsonArray array = mobject.getAsJsonArray(setting.getName());
                        if (array.get(0).getAsString().contains("M")) {
                            setting.setValue(new Bind(Integer.parseInt(array.get(0).getAsString().replace("M", "")), true, array.get(1).getAsBoolean()));
                        } else {
                            setting.setValue(new Bind(Integer.parseInt(array.get(0).getAsString()), false, array.get(1).getAsBoolean()));
                        }
                    } else if (setting.getValue() instanceof ColorSetting colorSetting) {
                        JsonArray array = mobject.getAsJsonArray(setting.getName());
                        colorSetting.setColor(array.get(0).getAsInt());
                        colorSetting.setRainbow(array.get(1).getAsBoolean());
                        colorSetting.setGlobalOffset(array.get(2).getAsInt());
                    } else if (setting.getValue() instanceof PositionSetting posSetting) {
                        JsonArray array = mobject.getAsJsonArray(setting.getName());
                        posSetting.setX(array.get(0).getAsFloat());
                        posSetting.setY(array.get(1).getAsFloat());
                    } else if (setting.getValue() instanceof BooleanSettingGroup bGroup) {
                        bGroup.setEnabled(mobject.getAsJsonPrimitive(setting.getName()).getAsBoolean());
                    } else if (setting.getValue() instanceof ItemSelectSetting iSetting) {
                        JsonArray array = mobject.getAsJsonArray(setting.getName());
                        for (int i = 0; i < array.size(); i++)
                            iSetting.getItemsById().add(array.get(i).getAsString());
                    } else if (setting.getValue().getClass().isEnum()) {
                        Enum value = new EnumConverter(((Enum) setting.getValue()).getClass()).doBackward(mobject.getAsJsonPrimitive(setting.getName()));
                        setting.setValue((value == null) ? setting.getDefaultValue() : value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private @NotNull JsonArray getModuleArray() {
        JsonArray modulesArray = new JsonArray();
        for (Module m : ThunderHack.moduleManager.modules) {
            modulesArray.add(getModuleObject(m));
        }
        return modulesArray;
    }

    public JsonObject getModuleObject(@NotNull Module m) {
        JsonObject attribs = new JsonObject();
        JsonParser jp = new JsonParser();

        for (Setting setting : m.getSettings()) {
            if (setting.getValue() instanceof ColorSetting color) {
                JsonArray array = new JsonArray();
                array.add(new JsonPrimitive(color.getRawColor()));
                array.add(new JsonPrimitive(color.isRainbow()));
                array.add(new JsonPrimitive(color.getGlobalOffset()));
                attribs.add(setting.getName(), array);
            } else if (setting.getValue() instanceof PositionSetting pos) {
                JsonArray array = new JsonArray();
                array.add(new JsonPrimitive(pos.getX()));
                array.add(new JsonPrimitive(pos.getY()));
                attribs.add(setting.getName(), array);
            } else if (setting.getValue() instanceof BooleanSettingGroup bGroup) {
                attribs.add(setting.getName(), jp.parse(String.valueOf(bGroup.isEnabled())));
            } else if (setting.getValue() instanceof Bind b) {
                JsonArray array = new JsonArray();
                if (b.isMouse())
                    array.add(jp.parse(b.getBind()));
                else
                    array.add(new JsonPrimitive(b.getKey()));
                array.add(new JsonPrimitive(b.isHold()));
                attribs.add(setting.getName(), array);
            } else if (setting.getValue() instanceof String str) {
                try {
                    attribs.add(setting.getName(), jp.parse(str.replace(" ", "%%").replace("/", "++")));
                } catch (Exception exception) {
                }
            } else if (setting.getValue() instanceof ItemSelectSetting iSelect) {
                JsonArray array = new JsonArray();
                for (String id : iSelect.getItemsById())
                    array.add(new JsonPrimitive(id));
                attribs.add(setting.getName(), array);
            } else if (setting.isEnumSetting()) {
                attribs.add(setting.getName(), new EnumConverter(((Enum) setting.getValue()).getClass()).doForward((Enum) setting.getValue()));
            } else {
                try {
                    attribs.add(setting.getName(), jp.parse(setting.getValue().toString()));
                } catch (Exception ignored) {
                }
            }
        }

        JsonObject moduleObject = new JsonObject();
        moduleObject.add(m.getName(), attribs);
        return moduleObject;
    }

    public boolean delete(@NotNull File file) {
        return file.delete();
    }

    public boolean delete(String name) {
        File file = new File(CONFIGS_FOLDER, name + ".th");
        if (!file.exists()) {
            return false;
        }
        return delete(file);
    }

    public List<String> getConfigList() {
        if (!MAIN_FOLDER.exists() || MAIN_FOLDER.listFiles() == null) return null;

        List<String> list = new ArrayList<>();

        if (CONFIGS_FOLDER.listFiles() != null) {
            for (File file : Arrays.stream(Objects.requireNonNull(CONFIGS_FOLDER.listFiles())).filter(f -> f.getName().endsWith(".th")).collect(Collectors.toList())) {
                list.add(file.getName().replace(".th", ""));
            }
        }
        return list;
    }

    public List<String> getCloudConfigs() {
        List<String> list = new ArrayList<>();
        try {
            URL url = new URL("https://raw.githubusercontent.com/Pan4ur/THRecodeUtil/main/cloudConfigs.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                list.add(inputLine.trim());
        } catch (Exception ignored) {
        }
        return list;
    }

    public void saveCurrentConfig() {
        File file = new File(ConfigManager.MAIN_FOLDER + "/misc/currentcfg.txt");
        try {
            if (file.exists()) {
                FileWriter writer = new FileWriter(file);
                writer.write(currentConfig.getName().replace(".th", ""));
                writer.close();
            } else {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(currentConfig.getName().replace(".th", ""));
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getCurrentConfig() {
        File file = new File(ConfigManager.MAIN_FOLDER + "/misc/currentcfg.txt");
        String name = "config";
        try {
            if (file.exists()) {
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine())
                    name = reader.nextLine();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentConfig = new File(CONFIGS_FOLDER, name + ".th");
        return currentConfig;
    }
}