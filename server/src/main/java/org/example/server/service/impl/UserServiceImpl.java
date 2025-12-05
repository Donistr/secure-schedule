package org.example.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.service.UserService;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SimpUserRegistry simpUserRegistry;

    @Override
    public boolean isUserOnline(String username) {
        return simpUserRegistry.getUser(username) != null;
    }

}
