package com.cityride.notification;

import com.cityride.common.exception.ResourceNotFoundException;
import com.cityride.notification.dto.NotificationResponse;
import com.cityride.user.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /** Cree une notification. Appele par les autres services (reservation, paiement, avis). */
    @Transactional
    public void notify(User recipient, NotificationType type, String message, Long referenceId) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(type);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMine(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getType(), n.getMessage(),
                        n.getReferenceId(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", notificationId));
        if (!n.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Cette notification ne vous appartient pas");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }
}
