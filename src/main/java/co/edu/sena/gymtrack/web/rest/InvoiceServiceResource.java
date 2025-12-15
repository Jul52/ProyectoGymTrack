package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.InvoiceServiceRepository;
import co.edu.sena.gymtrack.security.AuthoritiesConstants;
import co.edu.sena.gymtrack.service.InvoiceServiceService;
import co.edu.sena.gymtrack.service.dto.InvoiceServiceDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.edu.sena.gymtrack.domain.InvoiceService}.
 */
@RestController
@RequestMapping("/api/invoice-services")
public class InvoiceServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceServiceResource.class);

    private static final String ENTITY_NAME = "invoiceService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceServiceService invoiceServiceService;

    private final InvoiceServiceRepository invoiceServiceRepository;

    public InvoiceServiceResource(InvoiceServiceService invoiceServiceService, InvoiceServiceRepository invoiceServiceRepository) {
        this.invoiceServiceService = invoiceServiceService;
        this.invoiceServiceRepository = invoiceServiceRepository;
    }

    /**
     * {@code POST  /invoice-services} : Create a new invoiceService.
     *
     * @param invoiceServiceDTO the invoiceServiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoiceServiceDTO, or with status {@code 400 (Bad Request)} if the invoiceService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<InvoiceServiceDTO> createInvoiceService(@Valid @RequestBody InvoiceServiceDTO invoiceServiceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save InvoiceService : {}", invoiceServiceDTO);
        if (invoiceServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new invoiceService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        invoiceServiceDTO = invoiceServiceService.save(invoiceServiceDTO);
        return ResponseEntity.created(new URI("/api/invoice-services/" + invoiceServiceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, invoiceServiceDTO.getId().toString()))
            .body(invoiceServiceDTO);
    }

    /**
     * {@code PUT  /invoice-services/:id} : Updates an existing invoiceService.
     *
     * @param id the id of the invoiceServiceDTO to save.
     * @param invoiceServiceDTO the invoiceServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceServiceDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceServiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoiceServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<InvoiceServiceDTO> updateInvoiceService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InvoiceServiceDTO invoiceServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InvoiceService : {}, {}", id, invoiceServiceDTO);
        if (invoiceServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!invoiceServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        invoiceServiceDTO = invoiceServiceService.update(invoiceServiceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoiceServiceDTO.getId().toString()))
            .body(invoiceServiceDTO);
    }

    /**
     * {@code PATCH  /invoice-services/:id} : Partial updates given fields of an existing invoiceService, field will ignore if it is null
     *
     * @param id the id of the invoiceServiceDTO to save.
     * @param invoiceServiceDTO the invoiceServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceServiceDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceServiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the invoiceServiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the invoiceServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<InvoiceServiceDTO> partialUpdateInvoiceService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InvoiceServiceDTO invoiceServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InvoiceService partially : {}, {}", id, invoiceServiceDTO);
        if (invoiceServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!invoiceServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InvoiceServiceDTO> result = invoiceServiceService.partialUpdate(invoiceServiceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoiceServiceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /invoice-services} : get all the invoiceServices.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoiceServices in body.
     */
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
    public ResponseEntity<List<InvoiceServiceDTO>> getAllInvoiceServices(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of InvoiceServices");
        Page<InvoiceServiceDTO> page = invoiceServiceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /invoice-services/:id} : get the "id" invoiceService.
     *
     * @param id the id of the invoiceServiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoiceServiceDTO, or with status {@code 404 (Not Found)}.
     */
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
    public ResponseEntity<InvoiceServiceDTO> getInvoiceService(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InvoiceService : {}", id);
        Optional<InvoiceServiceDTO> invoiceServiceDTO = invoiceServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(invoiceServiceDTO);
    }

    /**
     * {@code DELETE  /invoice-services/:id} : delete the "id" invoiceService.
     *
     * @param id the id of the invoiceServiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<Void> deleteInvoiceService(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InvoiceService : {}", id);
        invoiceServiceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
