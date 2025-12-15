package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.PaymentRepository;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.security.AuthoritiesConstants;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.PaymentService;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.mapper.UserDataMapper;
import co.edu.sena.gymtrack.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserDataMapper userDataMapper;

    public PaymentResource(
        PaymentService paymentService,
        PaymentRepository paymentRepository,
        UserDataRepository userDataRepository,
        UserDataMapper userDataMapper
    ) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.userDataRepository = userDataRepository;
        this.userDataMapper = userDataMapper;
    }

    @PostMapping("")
    @PreAuthorize(
        "hasAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\") or hasAuthority(\"" +
        AuthoritiesConstants.TRAINER +
        "\") or hasAuthority(\"" +
        AuthoritiesConstants.USER +
        "\")"
    )
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Payment : {}", paymentDTO);

        // --- 1. VALIDACIÓN ID (Autogeneración) ---
        if (paymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new payment cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (paymentDTO.getRegisteredBy() == null) {
            SecurityUtils.getCurrentUserLogin()
                .flatMap(userDataRepository::findByUserLogin) // <-- AHORA USA EL MÉTODO EAGERLY CARGADO
                .ifPresent(userData -> paymentDTO.setRegisteredBy(userDataMapper.toDto(userData)));
        }

        // Opcional: Si el campo debe ser OBLIGATORIO en la BD, se lanza error si no se encontró usuario logueado.
        if (paymentDTO.getRegisteredBy() == null) {
            throw new BadRequestAlertException("Payment must be registered by a valid logged-in user.", ENTITY_NAME, "registeredbynull");
        }
        // -----------------------------------------------------------------------------------

        PaymentDTO result = paymentService.save(paymentDTO);
        return ResponseEntity.created(new URI("/api/payments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<PaymentDTO> updatePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentDTO paymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Payment : {}, {}", id, paymentDTO);
        if (paymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paymentDTO = paymentService.update(paymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentDTO.getId().toString()))
            .body(paymentDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<PaymentDTO> partialUpdatePayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentDTO paymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Payment partially : {}, {}", id, paymentDTO);
        if (paymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentDTO> result = paymentService.partialUpdate(paymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentDTO.getId().toString())
        );
    }

    @GetMapping("")
    @PreAuthorize(
        "hasAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\") or hasAuthority(\"" +
        AuthoritiesConstants.TRAINER +
        "\") or hasAuthority(\"" +
        AuthoritiesConstants.USER +
        "\")"
    )
    public ResponseEntity<List<PaymentDTO>> getAllPayments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("invoice-is-null".equals(filter)) {
            LOG.debug("REST request to get all Payments where invoice is null");
            return new ResponseEntity<>(paymentService.findAllWhereInvoiceIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Payments");
        Page<PaymentDTO> page;
        if (eagerload) {
            page = paymentService.findAllWithEagerRelationships(pageable);
        } else {
            page = paymentService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    @PreAuthorize(
        "hasAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\") or hasAuthority(\"" +
        AuthoritiesConstants.TRAINER +
        "\") or hasAuthority(\"" +
        AuthoritiesConstants.USER +
        "\")"
    )
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Payment : {}", id);
        Optional<PaymentDTO> paymentDTO = paymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Payment : {}", id);
        paymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
