package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.domain.*;
import co.edu.sena.gymtrack.repository.*;
import co.edu.sena.gymtrack.security.SecurityUtils;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentCheckoutResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentCheckoutResource.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final GymServiceRepository gymServiceRepository;
    private final UserDataRepository userDataRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceServiceRepository invoiceServiceRepository;

    public PaymentCheckoutResource(
        PaymentRepository paymentRepository,
        PaymentMethodRepository paymentMethodRepository,
        GymServiceRepository gymServiceRepository,
        UserDataRepository userDataRepository,
        InvoiceRepository invoiceRepository,
        InvoiceServiceRepository invoiceServiceRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.gymServiceRepository = gymServiceRepository;
        this.userDataRepository = userDataRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceServiceRepository = invoiceServiceRepository;
    }

    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody CheckoutRequest request) {
        LOG.debug("REST request to checkout service {} with method {}", request.serviceId(), request.paymentMethodId());

        // 1. Obtener usuario actual
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        UserData userData = userDataRepository
            .findOneByUserLogin(login)
            .orElseThrow(() -> new RuntimeException("Datos de usuario no encontrados para: " + login));

        // 2. Obtener servicio
        GymService service = gymServiceRepository
            .findById(request.serviceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + request.serviceId()));

        // 3. Obtener método de pago
        PaymentMethod paymentMethod = paymentMethodRepository
            .findById(request.paymentMethodId())
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + request.paymentMethodId()));

        // 4. Crear Payment
        String transactionId = "TRANS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment();
        payment.setAmountPaid(service.getPrice());
        payment.setPaymentDate(Instant.now());
        payment.setTransactionId(transactionId);
        payment.setStatus("COMPLETED");
        payment.setPaymentMethod(paymentMethod);
        payment.setRegisteredBy(userData);
        payment = paymentRepository.save(payment);

        // 5. Crear Invoice
        Invoice invoice = new Invoice();
        invoice.setTotal(service.getPrice());
        invoice.setCreatedDate(Instant.now());
        invoice.setPaymentMethod(paymentMethod);
        invoice.setUserData(userData);
        invoice.setPayment(payment);
        invoice.setService(service);
        invoice = invoiceRepository.save(invoice);

        // 6. Crear InvoiceService
        InvoiceService invoiceService = new InvoiceService();
        invoiceService.setInvoice(invoice);
        invoiceService.setService(service);
        invoiceService.setQuantity(1);
        invoiceService.setSalePrice(service.getPrice());
        invoiceService.setSubtotal(service.getPrice());
        invoiceServiceRepository.save(invoiceService);

        // 7. ¿El servicio incluye cursos?
        boolean hasCourses =
            service.getCourseAccessType() != null &&
            !service.getCourseAccessType().name().equals("NONE") &&
            !service.getCourses().isEmpty();

        // 8. Respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("transactionId", transactionId);
        response.put("paymentId", payment.getId());
        response.put("invoiceId", invoice.getId());
        response.put("amount", service.getPrice());
        response.put("serviceName", service.getServiceName());
        response.put("serviceId", service.getId());
        response.put("hasCourses", hasCourses);
        response.put("status", "COMPLETED");

        LOG.info("Checkout exitoso: transactionId={}, user={}, service={}", transactionId, login, service.getServiceName());

        return ResponseEntity.ok(response);
    }

    public record CheckoutRequest(Long serviceId, Long paymentMethodId) {}
}
