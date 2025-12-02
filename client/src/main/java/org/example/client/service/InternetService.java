package org.example.client.service;

import org.example.client.exception.RunCommandException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class InternetService {

    public void disableInternet() {
        System.out.printf("%s - интернет выключен%n", LocalDateTime.now());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        //runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -ne 'Disabled'} | Disable-NetAdapter -Confirm:$false");
    }

    public void enableInternet() {
        System.out.printf("%s - интернет включен%n", LocalDateTime.now());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        //runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -eq 'Disabled'} | Enable-NetAdapter -Confirm:$false");
    }

    private void runCommand(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RunCommandException(e);
        }
    }

}
