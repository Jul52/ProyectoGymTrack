package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.MachineIncidentsRepository;
import co.edu.sena.gymtrack.security.AuthoritiesConstants;
import co.edu.sena.gymtrack.service.MachineIncidentsService;
import co.edu.sena.gymtrack.service.dto.MachineIncidentsDTO;
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
 * REST controller for managing {@link co.edu.sena.gymtrack.domain.MachineIncidents}.
 */
@RestController
@RequestMapping("/api/machine-incidents")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
public class MachineIncidentsResource {

    private static final Logger LOG = LoggerFactory.getLogger(MachineIncidentsResource.class);

    private static final String ENTITY_NAME = "machineIncidents";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MachineIncidentsService machineIncidentsService;

    private final MachineIncidentsRepository machineIncidentsRepository;

    public MachineIncidentsResource(
        MachineIncidentsService machineIncidentsService,
        MachineIncidentsRepository machineIncidentsRepository
    ) {
        this.machineIncidentsService = machineIncidentsService;
        this.machineIncidentsRepository = machineIncidentsRepository;
    }

    /**
     * {@code POST  /machine-incidents} : Create a new machineIncidents.
     *
     * @param machineIncidentsDTO the machineIncidentsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new machineIncidentsDTO, or with status {@code 400 (Bad Request)} if the machineIncidents has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<MachineIncidentsDTO> createMachineIncidents(@Valid @RequestBody MachineIncidentsDTO machineIncidentsDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MachineIncidents : {}", machineIncidentsDTO);
        if (machineIncidentsDTO.getId() != null) {
            throw new BadRequestAlertException("A new machineIncidents cannot already have an ID", ENTITY_NAME, "idexists");
        }
        machineIncidentsDTO = machineIncidentsService.save(machineIncidentsDTO);
        return ResponseEntity.created(new URI("/api/machine-incidents/" + machineIncidentsDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, machineIncidentsDTO.getId().toString()))
            .body(machineIncidentsDTO);
    }

    /**
     * {@code PUT  /machine-incidents/:id} : Updates an existing machineIncidents.
     *
     * @param id the id of the machineIncidentsDTO to save.
     * @param machineIncidentsDTO the machineIncidentsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machineIncidentsDTO,
     * or with status {@code 400 (Bad Request)} if the machineIncidentsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the machineIncidentsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<MachineIncidentsDTO> updateMachineIncidents(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MachineIncidentsDTO machineIncidentsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MachineIncidents : {}, {}", id, machineIncidentsDTO);
        if (machineIncidentsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machineIncidentsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineIncidentsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        machineIncidentsDTO = machineIncidentsService.update(machineIncidentsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, machineIncidentsDTO.getId().toString()))
            .body(machineIncidentsDTO);
    }

    /**
     * {@code PATCH  /machine-incidents/:id} : Partial updates given fields of an existing machineIncidents, field will ignore if it is null
     *
     * @param id the id of the machineIncidentsDTO to save.
     * @param machineIncidentsDTO the machineIncidentsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machineIncidentsDTO,
     * or with status {@code 400 (Bad Request)} if the machineIncidentsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the machineIncidentsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the machineIncidentsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<MachineIncidentsDTO> partialUpdateMachineIncidents(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MachineIncidentsDTO machineIncidentsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MachineIncidents partially : {}, {}", id, machineIncidentsDTO);
        if (machineIncidentsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machineIncidentsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineIncidentsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MachineIncidentsDTO> result = machineIncidentsService.partialUpdate(machineIncidentsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, machineIncidentsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /machine-incidents} : get all the machineIncidents.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of machineIncidents in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<List<MachineIncidentsDTO>> getAllMachineIncidents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of MachineIncidents");
        Page<MachineIncidentsDTO> page;
        if (eagerload) {
            page = machineIncidentsService.findAllWithEagerRelationships(pageable);
        } else {
            page = machineIncidentsService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /machine-incidents/:id} : get the "id" machineIncidents.
     *
     * @param id the id of the machineIncidentsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the machineIncidentsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<MachineIncidentsDTO> getMachineIncidents(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MachineIncidents : {}", id);
        Optional<MachineIncidentsDTO> machineIncidentsDTO = machineIncidentsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(machineIncidentsDTO);
    }

    /**
     * {@code DELETE  /machine-incidents/:id} : delete the "id" machineIncidents.
     *
     * @param id the id of the machineIncidentsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Void> deleteMachineIncidents(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MachineIncidents : {}", id);
        machineIncidentsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
