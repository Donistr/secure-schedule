package org.example.client.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client.internet.InternetState;
import org.example.client.exception.RunCommandException;
import org.example.client.internet.InternetStateChangedEvent;
import org.example.client.service.InternetService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InternetServiceImpl implements InternetService {

    private final ApplicationEventPublisher eventPublisher;

    private InternetState state;

    @Override
    public synchronized void disableInternet() {
        if (state == InternetState.DISABLED) {
            return;
        }

        System.out.printf("%s - интернет выключается%n", LocalDateTime.now());

        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }*/

        runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -ne 'Disabled'} | Disable-NetAdapter -Confirm:$false");
        changeState(InternetState.DISABLED);

        System.out.printf("%s - интернет выключен%n", LocalDateTime.now());
    }

    @Override
    public synchronized void enableInternet() {
        if (state == InternetState.ENABLED) {
            return;
        }

        System.out.printf("%s - интернет включается%n", LocalDateTime.now());

        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }*/

        runCommand("powershell", "-Command", "Get-NetAdapter | Where-Object {$_.Status -eq 'Disabled'} | Enable-NetAdapter -Confirm:$false");
        waitInternetAccess();
        changeState(InternetState.ENABLED);

        System.out.printf("%s - интернет включен%n", LocalDateTime.now());
    }

    @Override
    public InternetState getState() {
        return state;
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

    private void changeState(InternetState newState) {
        if (newState == state) {
            return;
        }

        eventPublisher.publishEvent(new InternetStateChangedEvent(this, newState));
        state = newState;
    }

}
