package org.example.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.client.service.ScheduleService;
import org.example.client.service.impl.ChatService;
import org.example.shared.dto.ScheduleDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class Main {

    private final ChatService chatService;

    private final ScheduleService scheduleService;

    @PostConstruct
    private void postConstruct() {
        new Thread(this::startUserInputListening).start();
    }

    public void startUserInputListening() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Напишите 'exit' чтобы выйти");
        System.out.println("Формат сообщений: 'имя_получатель сообщение'");
        while (true) {
            try {
                String line = scanner.nextLine();
                if ("exit".equalsIgnoreCase(line)) {
                    scheduleService.setSchedule(new ScheduleDto(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), List.of()));
                    System.exit(0);
                }

                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2) {
                    System.out.println("Неверный формат");
                    continue;
                }

                String to = parts[0];
                String text = parts[1];
                chatService.sendMessage(to, text);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
