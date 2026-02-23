package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.domain.Invoice;
import co.edu.sena.gymtrack.domain.Payment;
import co.edu.sena.gymtrack.repository.GymServiceRepository;
import co.edu.sena.gymtrack.repository.InvoiceRepository;
import co.edu.sena.gymtrack.repository.PaymentMethodRepository;
import co.edu.sena.gymtrack.repository.PaymentRepository;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.PaymentService;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.mapper.PaymentMapper;
import co.edu.sena.gymtrack.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/payments")
public class PaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentResource.class);
    private static final String ENTITY_NAME = "payment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final UserDataRepository userDataRepository;
    private final GymServiceRepository gymServiceRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;

    public PaymentResource(
        PaymentService paymentService,
        PaymentRepository paymentRepository,
        UserDataRepository userDataRepository,
        GymServiceRepository gymServiceRepository,
        PaymentMethodRepository paymentMethodRepository,
        InvoiceRepository invoiceRepository,
        PaymentMapper paymentMapper
    ) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.userDataRepository = userDataRepository;
        this.gymServiceRepository = gymServiceRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentMapper = paymentMapper;
    }

    /**
     * POST /api/payments/checkout
     * Crea un pago y genera la factura automáticamente para el usuario autenticado.
     * Body: { "serviceId": 1, "paymentMethodId": 2 }
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Long> body) throws URISyntaxException {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();

        Long serviceId = body.get("serviceId");
        Long paymentMethodId = body.get("paymentMethodId");

        if (serviceId == null || paymentMethodId == null) {
            throw new BadRequestAlertException("serviceId y paymentMethodId son requeridos", ENTITY_NAME, "missingfields");
        }

        var userData = userDataRepository
            .findOneByUserLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("Usuario no encontrado", ENTITY_NAME, "usernotfound"));

        var gymService = gymServiceRepository
            .findById(serviceId)
            .orElseThrow(() -> new BadRequestAlertException("Servicio no encontrado", ENTITY_NAME, "servicenotfound"));

        var paymentMethod = paymentMethodRepository
            .findById(paymentMethodId)
            .orElseThrow(() -> new BadRequestAlertException("Método de pago no encontrado", ENTITY_NAME, "methodnotfound"));

        // Crear el pago
        Payment payment = new Payment();
        payment.setAmountPaid(gymService.getPrice());
        payment.setPaymentDate(Instant.now());
        payment.setTransactionId(UUID.randomUUID().toString().substring(0, 20));
        payment.setStatus("COMPLETED");
        payment.setPaymentMethod(paymentMethod);
        payment.setRegisteredBy(userData);
        payment = paymentRepository.save(payment);

        // Crear la factura vinculada al pago
        Invoice invoice = new Invoice();
        invoice.setTotal(gymService.getPrice());
        invoice.setCreatedDate(Instant.now());
        invoice.setPaymentMethod(paymentMethod);
        invoice.setUserData(userData);
        invoice.setService(gymService);
        invoice.setPayment(payment);
        invoiceRepository.save(invoice);

        return ResponseEntity.created(new URI("/api/payments/" + payment.getId())).body(
            Map.of("paymentId", payment.getId(), "status", "COMPLETED", "message", "Pago realizado exitosamente")
        );
    }

    /**
     * GET /api/payments/my — pagos del usuario autenticado
     */
    @GetMapping("/my")
    public ResponseEntity<List<PaymentDTO>> getMyPayments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        Page<PaymentDTO> page = paymentRepository.findAllByUserLogin(login, pageable).map(paymentMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("")
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Payment : {}", paymentDTO);
        if (paymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new payment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paymentDTO = paymentService.save(paymentDTO);
        return ResponseEntity.created(new URI("/api/payments/" + paymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paymentDTO.getId().toString()))
            .body(paymentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentDTO paymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Payment : {}, {}", id, paymentDTO);
        if (paymentDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, paymentDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!paymentRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        paymentDTO = paymentService.update(paymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentDTO.getId().toString()))
            .body(paymentDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentDTO> partialUpdatePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentDTO paymentDTO
    ) throws URISyntaxException {
        if (paymentDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, paymentDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!paymentRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        Optional<PaymentDTO> result = paymentService.partialUpdate(paymentDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<PaymentDTO>> getAllPayments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("invoice-is-null".equals(filter)) {
            return new ResponseEntity<>(paymentService.findAllWhereInvoiceIsNull(), HttpStatus.OK);
        }
        Page<PaymentDTO> page = eagerload ? paymentService.findAllWithEagerRelationships(pageable) : paymentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable("id") Long id) {
        Optional<PaymentDTO> paymentDTO = paymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
