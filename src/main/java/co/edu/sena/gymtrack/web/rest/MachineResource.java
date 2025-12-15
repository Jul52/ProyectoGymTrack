package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.MachineRepository;
import co.edu.sena.gymtrack.security.AuthoritiesConstants;
import co.edu.sena.gymtrack.service.MachineService;
import co.edu.sena.gymtrack.service.dto.MachineDTO;
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
 * REST controller for managing {@link co.edu.sena.gymtrack.domain.Machine}.
 */
@RestController
@RequestMapping("/api/machines")
public class MachineResource {

    private static final Logger LOG = LoggerFactory.getLogger(MachineResource.class);

    private static final String ENTITY_NAME = "machine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MachineService machineService;

    private final MachineRepository machineRepository;

    public MachineResource(MachineService machineService, MachineRepository machineRepository) {
        this.machineService = machineService;
        this.machineRepository = machineRepository;
    }

    /**
     * {@code POST  /machines} : Create a new machine.
     *
     * @param machineDTO the machineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new machineDTO, or with status {@code 400 (Bad Request)} if the machine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<MachineDTO> createMachine(@Valid @RequestBody MachineDTO machineDTO) throws URISyntaxException {
        LOG.debug("REST request to save Machine : {}", machineDTO);
        if (machineDTO.getId() != null) {
            throw new BadRequestAlertException("A new machine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        machineDTO = machineService.save(machineDTO);
        return ResponseEntity.created(new URI("/api/machines/" + machineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, machineDTO.getId().toString()))
            .body(machineDTO);
    }

    /**
     * {@code PUT  /machines/:id} : Updates an existing machine.
     *
     * @param id the id of the machineDTO to save.
     * @param machineDTO the machineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machineDTO,
     * or with status {@code 400 (Bad Request)} if the machineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the machineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<MachineDTO> updateMachine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MachineDTO machineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Machine : {}, {}", id, machineDTO);
        if (machineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        machineDTO = machineService.update(machineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, machineDTO.getId().toString()))
            .body(machineDTO);
    }

    /**
     * {@code PATCH  /machines/:id} : Partial updates given fields of an existing machine, field will ignore if it is null
     *
     * @param id the id of the machineDTO to save.
     * @param machineDTO the machineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machineDTO,
     * or with status {@code 400 (Bad Request)} if the machineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the machineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the machineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<MachineDTO> partialUpdateMachine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MachineDTO machineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Machine partially : {}, {}", id, machineDTO);
        if (machineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MachineDTO> result = machineService.partialUpdate(machineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, machineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /machines} : get all the machines.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of machines in body.
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
    public ResponseEntity<List<MachineDTO>> getAllMachines(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Machines");
        Page<MachineDTO> page;
        if (eagerload) {
            page = machineService.findAllWithEagerRelationships(pageable);
        } else {
            page = machineService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /machines/:id} : get the "id" machine.
     *
     * @param id the id of the machineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the machineDTO, or with status {@code 404 (Not Found)}.
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
    public ResponseEntity<MachineDTO> getMachine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Machine : {}", id);
        Optional<MachineDTO> machineDTO = machineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(machineDTO);
    }

    /**
     * {@code DELETE  /machines/:id} : delete the "id" machine.
     *
     * @param id the id of the machineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<Void> deleteMachine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Machine : {}", id);
        machineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
