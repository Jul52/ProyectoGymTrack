package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.Reservation;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.ReservationRepository;
import co.edu.sena.gymtrack.repository.UserDataRepository; // Nueva Importación
import co.edu.sena.gymtrack.security.SecurityUtils; // Nueva Importación
import co.edu.sena.gymtrack.service.ReservationService;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.mapper.ReservationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.Reservation}.
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserDataRepository userDataRepository; // Nueva dependencia

    public ReservationServiceImpl(
        ReservationRepository reservationRepository,
        ReservationMapper reservationMapper,
        UserDataRepository userDataRepository // Inyección de dependencia
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.userDataRepository = userDataRepository;
    }

    @Override
    public ReservationDTO save(ReservationDTO reservationDTO) {
        LOG.debug("Request to save Reservation : {}", reservationDTO);

        // --- INYECCIÓN DE SEGURIDAD: Asignar al usuario logueado ---
        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin == null) {
            throw new RuntimeException("No hay usuario autenticado.");
        }

        Optional<UserData> registeredByUserData = userDataRepository.findOneByUserLogin(userLogin);

        if (registeredByUserData.isEmpty()) {
            throw new RuntimeException("Error: UserData no encontrado para el usuario: " + userLogin);
        }
        // -----------------------------------------------------------

        Reservation reservation = reservationMapper.toEntity(reservationDTO);

        // Asignar el UserData encontrado a la entidad
        reservation.setRegisteredBy(registeredByUserData.get()); // IMPORTANTE: Usar setRegisteredBy()

        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    @Override
    public ReservationDTO update(ReservationDTO reservationDTO) {
        LOG.debug("Request to update Reservation : {}", reservationDTO);
        // NOTA: En un update real, deberías verificar que el usuario logueado sea el dueño de la reserva o un ADMIN.
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    @Override
    public Optional<ReservationDTO> partialUpdate(ReservationDTO reservationDTO) {
        LOG.debug("Request to partially update Reservation : {}", reservationDTO);

        return reservationRepository
            .findById(reservationDTO.getId())
            .map(existingReservation -> {
                reservationMapper.partialUpdate(existingReservation, reservationDTO);

                return existingReservation;
            })
            .map(reservationRepository::save)
            .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Reservations");
        return reservationRepository.findAll(pageable).map(reservationMapper::toDto);
    }

    public Page<ReservationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reservationRepository.findAllWithEagerRelationships(pageable).map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationDTO> findOne(Long id) {
        LOG.debug("Request to get Reservation : {}", id);
        return reservationRepository.findOneWithEagerRelationships(id).map(reservationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Reservation : {}", id);
        reservationRepository.deleteById(id);
    }
}
