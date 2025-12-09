package org.example.server.service;

import org.example.shared.dto.TimePeriodDto;

import java.util.Optional;

public interface UserService {

    boolean isUserOnline(String username);

    Optional<TimePeriodDto> getCurrentActivePeriodForUser(String name);

    void disconnectUser(String name);

}
