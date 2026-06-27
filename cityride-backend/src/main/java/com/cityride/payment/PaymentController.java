package com.cityride.payment;

import com.cityride.payment.dto.CreatePaymentRequest;
import com.cityride.payment.dto.PaymentResponse;
import com.cityride.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Paiements", description = "Paiement d'une reservation et historique")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Payer une reservation confirmee")
    public PaymentResponse pay(@AuthenticationPrincipal CustomUserDetails principal,
                               @Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.pay(principal.getId(), request.bookingId());
    }

    @GetMapping("/mine")
    @Operation(summary = "Mon historique de paiements")
    public List<PaymentResponse> myPayments(@AuthenticationPrincipal CustomUserDetails principal) {
        return paymentService.getMyPayments(principal.getId());
    }
}
