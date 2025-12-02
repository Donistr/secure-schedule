package org.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        //disableInternet();
        //enableInternet();
        //System.out.println(getActiveInterfaceName());
    }

    private static void disableInternet() {
        runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -ne 'Disabled'} | Disable-NetAdapter -Confirm:$false");
        //runCommand("netsh", "interface", "set", "interface", "Ethernet", "admin=disabled");
    }

    private static void enableInternet() {
        runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -eq 'Disabled'} | Enable-NetAdapter -Confirm:$false");
        //runCommand("netsh", "interface", "set", "interface", "Ethernet", "admin=enabled");
    }

    private static void runCommand(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getActiveInterfaceName() {
        try {
            ProcessBuilder pb = new ProcessBuilder("netsh", "interface", "show", "interface");
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), "CP866")); // важно CP866!

            String line;
            String result = null;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // Ищем строку с "Enabled" и "Connected" (или "Подключено")
                if (line.contains("Enabled") && (line.contains("Connected") || line.contains("Подключено"))) {
                    // Имя интерфейса — последние слова в строке
                    String[] parts = line.trim().split("\\s+");
                    result = String.join(" ", java.util.Arrays.copyOfRange(parts, 3, parts.length));
                    System.out.println("Найден активный интерфейс: " + result);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
