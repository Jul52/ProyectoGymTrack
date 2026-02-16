package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Reservation;
import co.edu.sena.gymtrack.domain.Schedule;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.domain.enumeration.CourseAccessType;
import co.edu.sena.gymtrack.repository.ReservationRepository;
import co.edu.sena.gymtrack.repository.ScheduleRepository;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.ReservationService;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.mapper.ReservationMapper;
import co.edu.sena.gymtrack.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserDataRepository userDataRepository;
    private final ScheduleRepository scheduleRepository;

    public ReservationServiceImpl(
        ReservationRepository reservationRepository,
        ReservationMapper reservationMapper,
        UserDataRepository userDataRepository,
        ScheduleRepository scheduleRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.userDataRepository = userDataRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // ================= SAVE =================

    @Override
    public ReservationDTO save(ReservationDTO reservationDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (userLogin == null) {
            throw new RuntimeException("No hay usuario autenticado.");
        }

        UserData registeredBy = userDataRepository
            .findOneByUserLogin(userLogin)
            .orElseThrow(() -> new RuntimeException("UserData no encontrado para el usuario: " + userLogin));

        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        reservation.setRegisteredBy(registeredBy);

        if (reservation.getSchedule() == null || reservation.getSchedule().getId() == null) {
            throw new BadRequestAlertException("Debe seleccionar un horario.", "reservation", "scheduleRequired");
        }

        Schedule managedSchedule = scheduleRepository
            .findById(reservation.getSchedule().getId())
            .orElseThrow(() -> new BadRequestAlertException("Horario no encontrado.", "reservation", "scheduleNotFound"));

        reservation.setCourse(managedSchedule.getCourse());

        GymService gymService = reservation.getGymService();

        if (gymService.getCourseAccessType() == CourseAccessType.NONE) {
            throw new BadRequestAlertException("Este servicio no permite reservas de cursos.", "reservation", "noAccess");
        }

        if (gymService.getCourseAccessType() == CourseAccessType.LIMITED) {
            Integer max = gymService.getMaxReservationsPerCourse();

            if (max != null && max > 0) {
                long currentCount = reservationRepository.countActiveReservationsByUserServiceAndCourse(
                    registeredBy.getId(),
                    gymService.getId(),
                    managedSchedule.getCourse().getId()
                );

                if (currentCount >= max) {
                    throw new BadRequestAlertException(
                        "Ya alcanzaste el límite de reservas para este curso.",
                        "reservation",
                        "limitExceeded"
                    );
                }
            }
        }

        if (managedSchedule.getAvailableSlots() <= 0) {
            throw new BadRequestAlertException("No hay cupos disponibles para este horario.", "reservation", "noCapacity");
        }

        managedSchedule.setAvailableSlots(managedSchedule.getAvailableSlots() - 1);
        scheduleRepository.save(managedSchedule);

        reservation.setSchedule(managedSchedule);

        reservation = reservationRepository.save(reservation);

        return reservationMapper.toDto(reservation);
    }

    // ================= FIND ALL =================

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> findAll(Pageable pageable) {
        if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            return reservationRepository.findAllWithEagerRelationships(pageable).map(reservationMapper::toDto);
        }

        String login = SecurityUtils.getCurrentUserLogin().orElse(null);

        return reservationRepository.findAllByUserLogin(login, pageable).map(reservationMapper::toDto);
    }

    @Override
    public Optional<ReservationDTO> findOneWithEagerRelationships(Long id) {
        return reservationRepository.findOneWithEagerRelationships(id).map(reservationMapper::toDto);
    }

    // 🔥 ESTE ERA EL MÉTODO QUE FALTABA

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    // ================= FIND ONE =================

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationDTO> findOne(Long id) {
        return reservationRepository.findOneWithEagerRelationships(id).map(reservationMapper::toDto);
    }

    // ================= UPDATE =================

    @Override
    public ReservationDTO update(ReservationDTO reservationDTO) {
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    @Override
    public Optional<ReservationDTO> partialUpdate(ReservationDTO reservationDTO) {
        return reservationRepository
            .findById(reservationDTO.getId())
            .map(existingReservation -> {
                reservationMapper.partialUpdate(existingReservation, reservationDTO);
                return existingReservation;
            })
            .map(reservationRepository::save)
            .map(reservationMapper::toDto);
    }

    // ================= DELETE =================

    @Override
    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}
