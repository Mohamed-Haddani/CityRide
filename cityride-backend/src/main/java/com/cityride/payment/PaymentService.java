package com.cityride.payment;

import com.cityride.booking.Booking;
import com.cityride.booking.BookingService;
import com.cityride.booking.BookingStatus;
import com.cityride.common.exception.BusinessException;
import com.cityride.common.exception.ConflictException;
import com.cityride.notification.NotificationService;
import com.cityride.notification.NotificationType;
import com.cityride.payment.dto.PaymentResponse;
import com.cityride.payment.provider.PaymentProvider;
import com.cityride.payment.provider.PaymentResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final NotificationService notificationService;
    private final Map<PaymentProviderType, PaymentProvider> providers = new EnumMap<>(PaymentProviderType.class);
    private final PaymentProviderType activeProviderType;
    private final String currency;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingService bookingService,
                          NotificationService notificationService,
                          List<PaymentProvider> providerBeans,
                          @Value("${app.payment.provider:SIMULATED}") PaymentProviderType activeProviderType,
                          @Value("${app.payment.currency:MAD}") String currency) {
        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
        providerBeans.forEach(p -> providers.put(p.type(), p));
        this.activeProviderType = activeProviderType;
        this.currency = currency;
    }

    @Transactional
    public PaymentResponse pay(Long userId, Long bookingId) {
        Booking booking = bookingService.getEntity(bookingId);

        if (!booking.getPassenger().getId().equals(userId)) {
            throw new AccessDeniedException("Ce n'est pas votre reservation");
        }
        if (booking.getStatus() == BookingStatus.PAID
                || paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.SUCCEEDED)) {
            throw new ConflictException("Cette reservation est deja payee");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("La reservation doit etre confirmee par le conducteur avant le paiement");
        }

        PaymentProvider provider = resolveProvider();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setCurrency(currency);
        payment.setProvider(provider.type());
        payment.setStatus(PaymentStatus.PENDING);

        String description = "Reservation #" + booking.getId();
        PaymentResult result = provider.charge(booking.getTotalPrice(), currency, description);

        if (result.success()) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setProviderRef(result.providerRef());
            payment.setPaidAt(Instant.now());
            bookingService.markAsPaid(booking);
            notificationService.notify(booking.getRide().getDriver(), NotificationType.PAYMENT_CONFIRMED,
                    "Paiement recu (" + booking.getTotalPrice() + " " + currency + ") pour "
                            + booking.getRide().getDepartureCity() + " -> " + booking.getRide().getDestinationCity(),
                    booking.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return PaymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(Long userId) {
        return paymentRepository.findHistoryByUser(userId).stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    private PaymentProvider resolveProvider() {
        PaymentProvider provider = providers.get(activeProviderType);
        if (provider == null) {
            throw new BusinessException("Aucun fournisseur de paiement configure pour " + activeProviderType);
        }
        return provider;
    }
}
