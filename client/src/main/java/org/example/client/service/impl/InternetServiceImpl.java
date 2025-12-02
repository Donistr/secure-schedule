package org.example.client.service.impl;

import org.example.client.exception.RunCommandException;
import org.example.client.service.InternetService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
public class InternetServiceImpl implements InternetService {

    @Override
    public void disableInternet() {
        System.out.printf("%s - интернет выключен%n", LocalDateTime.now());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        //runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -ne 'Disabled'} | Disable-NetAdapter -Confirm:$false");
    }

    @Override
    public void enableInternet() {
        System.out.printf("%s - интернет включен%n", LocalDateTime.now());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        /*runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -eq 'Disabled'} | Enable-NetAdapter -Confirm:$false");
        waitInternetAccess();*/
    }

    private void waitInternetAccess() {
        boolean available = false;
        while (!available) {
            System.out.println(LocalDateTime.now());
            System.out.println("try");
            available = isInternetAvailable();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isInternetAvailable() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10053);
            return true;
        } catch (Exception ignored) {
            return false;
        }
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
