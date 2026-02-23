package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.GymServiceRepository;
import co.edu.sena.gymtrack.repository.InvoiceRepository;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.GymServiceService;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/gym-services")
public class GymServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(GymServiceResource.class);
    private static final String ENTITY_NAME = "gymService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GymServiceService gymServiceService;
    private final GymServiceRepository gymServiceRepository;
    private final InvoiceRepository invoiceRepository;

    public GymServiceResource(
        GymServiceService gymServiceService,
        GymServiceRepository gymServiceRepository,
        InvoiceRepository invoiceRepository
    ) {
        this.gymServiceService = gymServiceService;
        this.gymServiceRepository = gymServiceRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping("")
    public ResponseEntity<GymServiceDTO> createGymService(@Valid @RequestBody GymServiceDTO gymServiceDTO) throws URISyntaxException {
        LOG.debug("REST request to save GymService : {}", gymServiceDTO);
        if (gymServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new gymService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        gymServiceDTO = gymServiceService.save(gymServiceDTO);
        return ResponseEntity.created(new URI("/api/gym-services/" + gymServiceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, gymServiceDTO.getId().toString()))
            .body(gymServiceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymServiceDTO> updateGymService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GymServiceDTO gymServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update GymService : {}, {}", id, gymServiceDTO);
        if (gymServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gymServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!gymServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        gymServiceDTO = gymServiceService.update(gymServiceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gymServiceDTO.getId().toString()))
            .body(gymServiceDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GymServiceDTO> partialUpdateGymService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GymServiceDTO gymServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update GymService partially : {}, {}", id, gymServiceDTO);
        if (gymServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gymServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!gymServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<GymServiceDTO> result = gymServiceService.partialUpdate(gymServiceDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gymServiceDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<GymServiceDTO>> getAllGymServices(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of GymServices");
        Page<GymServiceDTO> page;
        if (eagerload) {
            page = gymServiceService.findAllWithEagerRelationships(pageable);
        } else {
            page = gymServiceService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymServiceDTO> getGymService(@PathVariable("id") Long id) {
        LOG.debug("REST request to get GymService : {}", id);
        Optional<GymServiceDTO> gymServiceDTO = gymServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gymServiceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGymService(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete GymService : {}", id);
        gymServiceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/my-status")
    public ResponseEntity<List<Map<String, Object>>> getMyServiceStatuses() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();

        List<GymServiceDTO> allServices = gymServiceService.findAll(Pageable.unpaged()).getContent();

        List<Object[]> purchases = invoiceRepository.findLatestPurchaseDateByUserLogin(login);
        Map<Long, Instant> lastPurchaseByService = new HashMap<>();
        for (Object[] row : purchases) {
            lastPurchaseByService.put((Long) row[0], (Instant) row[1]);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (GymServiceDTO svc : allServices) {
            Instant purchaseDate = lastPurchaseByService.get(svc.getId());
            Map<String, Object> entry = new HashMap<>();
            entry.put("serviceId", svc.getId());
            entry.put("serviceName", svc.getServiceName());
            if (purchaseDate == null) {
                entry.put("status", "NOT_PURCHASED");
                entry.put("purchaseDate", null);
                entry.put("expirationDate", null);
            } else {
                Instant expiration = purchaseDate.plus(30, ChronoUnit.DAYS);
                entry.put("status", Instant.now().isBefore(expiration) ? "ACTIVE" : "EXPIRED");
                entry.put("purchaseDate", purchaseDate);
                entry.put("expirationDate", expiration);
            }
            result.add(entry);
        }

        return ResponseEntity.ok(result);
    }
}
