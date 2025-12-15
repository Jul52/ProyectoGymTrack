package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.security.AuthoritiesConstants;
import co.edu.sena.gymtrack.service.UserDataService;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
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
 * REST controller for managing {@link co.edu.sena.gymtrack.domain.UserData}.
 */
@RestController
@RequestMapping("/api/user-data")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
public class UserDataResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataResource.class);

    private static final String ENTITY_NAME = "userData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserDataService userDataService;

    private final UserDataRepository userDataRepository;

    public UserDataResource(UserDataService userDataService, UserDataRepository userDataRepository) {
        this.userDataService = userDataService;
        this.userDataRepository = userDataRepository;
    }

    /**
     * {@code POST  /user-data} : Create a new userData.
     *
     * @param userDataDTO the userDataDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userDataDTO, or with status {@code 400 (Bad Request)} if the userData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<UserDataDTO> createUserData(@Valid @RequestBody UserDataDTO userDataDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserData : {}", userDataDTO);
        if (userDataDTO.getId() != null) {
            throw new BadRequestAlertException("A new userData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userDataDTO = userDataService.save(userDataDTO);
        return ResponseEntity.created(new URI("/api/user-data/" + userDataDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userDataDTO.getId().toString()))
            .body(userDataDTO);
    }

    /**
     * {@code PUT  /user-data/:id} : Updates an existing userData.
     *
     * @param id the id of the userDataDTO to save.
     * @param userDataDTO the userDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDataDTO,
     * or with status {@code 400 (Bad Request)} if the userDataDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<UserDataDTO> updateUserData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserDataDTO userDataDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserData : {}, {}", id, userDataDTO);
        if (userDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userDataDTO = userDataService.update(userDataDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userDataDTO.getId().toString()))
            .body(userDataDTO);
    }

    /**
     * {@code PATCH  /user-data/:id} : Partial updates given fields of an existing userData, field will ignore if it is null
     *
     * @param id the id of the userDataDTO to save.
     * @param userDataDTO the userDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDataDTO,
     * or with status {@code 400 (Bad Request)} if the userDataDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userDataDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<UserDataDTO> partialUpdateUserData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserDataDTO userDataDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserData partially : {}, {}", id, userDataDTO);
        if (userDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserDataDTO> result = userDataService.partialUpdate(userDataDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userDataDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-data} : get all the userData.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userData in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<List<UserDataDTO>> getAllUserData(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of UserData");
        Page<UserDataDTO> page;
        if (eagerload) {
            page = userDataService.findAllWithEagerRelationships(pageable);
        } else {
            page = userDataService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-data/:id} : get the "id" userData.
     *
     * @param id the id of the userDataDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<UserDataDTO> getUserData(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserData : {}", id);
        Optional<UserDataDTO> userDataDTO = userDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userDataDTO);
    }

    /**
     * {@code DELETE  /user-data/:id} : delete the "id" userData.
     *
     * @param id the id of the userDataDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") ")
    public ResponseEntity<Void> deleteUserData(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserData : {}", id);
        userDataService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
