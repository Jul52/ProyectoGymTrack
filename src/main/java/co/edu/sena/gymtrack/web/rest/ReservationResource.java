package co.edu.sena.gymtrack.web.rest;

import co.edu.sena.gymtrack.repository.GymServiceRepository;
import co.edu.sena.gymtrack.repository.ReservationRepository;
import co.edu.sena.gymtrack.repository.ScheduleRepository;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.ReservationService;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.dto.ScheduleDTO;
import co.edu.sena.gymtrack.service.mapper.GymServiceMapper;
import co.edu.sena.gymtrack.service.mapper.ScheduleMapper;
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
@RequestMapping("/api/reservations")
public class ReservationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationResource.class);
    private static final String ENTITY_NAME = "reservation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final GymServiceRepository gymServiceRepository;
    private final ScheduleRepository scheduleRepository;
    private final GymServiceMapper gymServiceMapper;
    private final ScheduleMapper scheduleMapper;

    public ReservationResource(
        ReservationService reservationService,
        ReservationRepository reservationRepository,
        GymServiceRepository gymServiceRepository,
        ScheduleRepository scheduleRepository,
        GymServiceMapper gymServiceMapper,
        ScheduleMapper scheduleMapper
    ) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.gymServiceRepository = gymServiceRepository;
        this.scheduleRepository = scheduleRepository;
        this.gymServiceMapper = gymServiceMapper;
        this.scheduleMapper = scheduleMapper;
    }

    @PostMapping("")
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) throws URISyntaxException {
        LOG.debug("REST request to save Reservation : {}", reservationDTO);
        if (reservationDTO.getId() != null) {
            throw new BadRequestAlertException("A new reservation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reservationDTO = reservationService.save(reservationDTO);
        return ResponseEntity.created(new URI("/api/reservations/" + reservationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, reservationDTO.getId().toString()))
            .body(reservationDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReservationDTO reservationDTO
    ) throws URISyntaxException {
        if (reservationDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, reservationDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!reservationRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        reservationDTO = reservationService.update(reservationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservationDTO.getId().toString()))
            .body(reservationDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReservationDTO> partialUpdateReservation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReservationDTO reservationDTO
    ) throws URISyntaxException {
        if (reservationDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, reservationDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!reservationRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        Optional<ReservationDTO> result = reservationService.partialUpdate(reservationDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservationDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        Page<ReservationDTO> page = eagerload
            ? reservationService.findAllWithEagerRelationships(pageable)
            : reservationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable("id") Long id) {
        Optional<ReservationDTO> reservationDTO = reservationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reservationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/my-services")
    public ResponseEntity<List<GymServiceDTO>> getMyServices() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();

        List<co.edu.sena.gymtrack.domain.GymService> services;

        if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            services = gymServiceRepository.findAll();
        } else {
            services = gymServiceRepository.findServicesByUserLogin(login);
        }

        List<GymServiceDTO> dtos = services.stream().map(gymServiceMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/available-schedules/{serviceId}")
    public ResponseEntity<List<ScheduleDTO>> getAvailableSchedules(@PathVariable Long serviceId) {
        List<ScheduleDTO> dtos = scheduleRepository.findAvailableSchedulesByService(serviceId).stream().map(scheduleMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
}
