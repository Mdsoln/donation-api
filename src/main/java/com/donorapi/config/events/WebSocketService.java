package com.donorapi.config.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Integer donorId, String message) {
        messagingTemplate.convertAndSendToUser(
                donorId.toString(),
                "/queue/notifications",
                new NotificationMessage(message)
        );
    }

    @Getter
    @AllArgsConstructor
    private static class NotificationMessage {
        private String content;
    }
}
