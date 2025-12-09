package org.example.server.config.socket;

import lombok.RequiredArgsConstructor;
import org.example.server.service.UserService;
import org.example.shared.dto.TimePeriodDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    private static final ZoneId TIME_ZONE = ZoneId.systemDefault();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        String name = servletRequest.getServletRequest().getParameter("name");
        if (name == null || name.isBlank()) {
            return false;
        }

        Optional<TimePeriodDto> currentActivePeriod = userService.getCurrentActivePeriodForUser(name);
        if (currentActivePeriod.isEmpty()) {
            return false;
        }

        long delay = currentActivePeriod.get().to().atZone(TIME_ZONE).toInstant().toEpochMilli() - LocalDateTime.now().atZone(TIME_ZONE).toInstant().toEpochMilli();
        scheduledExecutorService.schedule(() -> userService.disconnectUser(name), Math.max(0, delay), TimeUnit.MILLISECONDS);

        attributes.put("username", name);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

}
