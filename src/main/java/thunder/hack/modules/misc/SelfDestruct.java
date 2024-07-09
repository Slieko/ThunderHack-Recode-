package thunder.hack.modules.misc;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import thunder.hack.ThunderHack;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.modules.Module;
import thunder.hack.modules.client.ClientSettings;
import thunder.hack.setting.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SelfDestruct extends Module{
    public SelfDestruct(){
        super("SelfDestruct", Category.CLIENT);
    }

    @Override
    public void onEnable(){
     //   ModuleManager.selfDestruct.disable();
        ModuleManager.unHook.enable();
        cleanMemory();
    }

    public static int findMinecraftPID() throws IOException {
        String line;
        String pidInfo = "";

        Process p = Runtime.getRuntime().exec(System.getProperty("os.name").toLowerCase().contains("win")
                ? "tasklist.exe /fo csv /nh"
                : "ps -e");

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((line = input.readLine()) != null) {
            pidInfo += line;
        }

        input.close();

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String[] lines = pidInfo.split("\n");
            for (String proc : lines) {
                if (proc.contains("javaw.exe") && proc.contains("Minecraft")) {
                    String[] parts = proc.split(",");
                    return Integer.parseInt(parts[1].replaceAll("\"", ""));
                }
            }
        } else {
            String[] lines = pidInfo.split("\n");
            for (String proc : lines) {
                if (proc.contains("java") && proc.contains("Minecraft")) {
                    return Integer.parseInt(proc.trim().split("\\s+")[0]);
                }
            }
        }
        return -1;
    }

    public static void readAndCleanMemory(int pid) {
        WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_VM_READ | Kernel32.PROCESS_QUERY_INFORMATION, false, pid);

        if (process == null) {
            System.out.println("Cant find MC Process!");
            return;
        }

        // Пример чтения из базового адреса
        Pointer baseAddress = new Pointer(0x00000000);
        IntByReference bytesRead = new IntByReference(0);
        byte[] buffer1 = new byte[1024];
        Pointer buffer = new Pointer(1024);

        boolean result = Kernel32.INSTANCE.ReadProcessMemory(process, baseAddress, buffer, buffer1.length, bytesRead);

        if (result) {
            String memoryContent = new String(buffer1);
            int index = memoryContent.indexOf("thunderhack");
            if (index != -1) {
                System.out.println("Найдено 'thunderhack' в памяти процесса.");

                byte[] emptyBytes = new byte["thunderhack".length()];
                Pointer emptyBytes1 = new Pointer("thunderhack".length());
                Pointer writeAddress = baseAddress.share(index);
                boolean writeResult = Kernel32.INSTANCE.WriteProcessMemory(process, writeAddress, emptyBytes1, emptyBytes.length, null);
            }
            Kernel32.INSTANCE.CloseHandle(process);
        }
    }

    public void cleanMemory() {
        try {
            int pid = findMinecraftPID();
            if (pid != -1) {
                System.out.println("Minecraft PID: " + pid);
                readAndCleanMemory(pid);
            } else {
                System.out.println("Процесс Minecraft не найден.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
