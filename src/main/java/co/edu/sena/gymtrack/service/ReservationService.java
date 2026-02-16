package co.edu.sena.gymtrack.service;

import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.edu.sena.gymtrack.domain.Reservation}.
 */
public interface ReservationService {
    ReservationDTO save(ReservationDTO reservationDTO);
    ReservationDTO update(ReservationDTO reservationDTO);
    Optional<ReservationDTO> partialUpdate(ReservationDTO reservationDTO);
    Page<ReservationDTO> findAll(Pageable pageable);

    /**
     * Get all the reservations with eager load of one-to-one relationships.
     */
    Page<ReservationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" reservation with eager load of one-to-one relationships.
     */
    Optional<ReservationDTO> findOneWithEagerRelationships(Long id);

    Optional<ReservationDTO> findOne(Long id);
    void delete(Long id);
}
