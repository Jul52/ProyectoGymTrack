package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.InvoiceRepository;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.InvoiceService;
import co.edu.sena.gymtrack.service.dto.InvoiceDTO;
import co.edu.sena.gymtrack.service.mapper.InvoiceMapper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceResource.class);
    private static final String ENTITY_NAME = "invoice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceResource(InvoiceService invoiceService, InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @PostMapping("")
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Invoice : {}", invoiceDTO);
        if (invoiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new invoice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        invoiceDTO = invoiceService.save(invoiceDTO);
        return ResponseEntity.created(new URI("/api/invoices/" + invoiceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, invoiceDTO.getId().toString()))
            .body(invoiceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InvoiceDTO invoiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Invoice : {}, {}", id, invoiceDTO);
        if (invoiceDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, invoiceDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!invoiceRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        invoiceDTO = invoiceService.update(invoiceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoiceDTO.getId().toString()))
            .body(invoiceDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InvoiceDTO> partialUpdateInvoice(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InvoiceDTO invoiceDTO
    ) throws URISyntaxException {
        if (invoiceDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, invoiceDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!invoiceRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        Optional<InvoiceDTO> result = invoiceService.partialUpdate(invoiceDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoiceDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Invoices");
        Page<InvoiceDTO> page = eagerload ? invoiceService.findAllWithEagerRelationships(pageable) : invoiceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/my")
    public ResponseEntity<List<InvoiceDTO>> getMyInvoices(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        Page<InvoiceDTO> page = invoiceRepository.findAllByUserLogin(login, pageable).map(invoiceMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable("id") Long id) {
        Optional<InvoiceDTO> invoiceDTO = invoiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(invoiceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
