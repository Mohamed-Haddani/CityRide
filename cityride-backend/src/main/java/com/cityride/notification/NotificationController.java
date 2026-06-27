package com.cityride.notification;

import com.cityride.notification.dto.NotificationResponse;
import com.cityride.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notifications utilisateur (stockees en base)")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Mes notifications")
    public List<NotificationResponse> mine(@AuthenticationPrincipal CustomUserDetails principal) {
        return notificationService.getMine(principal.getId());
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Nombre de notifications non lues")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal CustomUserDetails principal) {
        return Map.of("count", notificationService.unreadCount(principal.getId()));
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Marquer une notification comme lue")
    public void markRead(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        notificationService.markRead(principal.getId(), id);
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Tout marquer comme lu")
    public void markAllRead(@AuthenticationPrincipal CustomUserDetails principal) {
        notificationService.markAllRead(principal.getId());
    }
}
