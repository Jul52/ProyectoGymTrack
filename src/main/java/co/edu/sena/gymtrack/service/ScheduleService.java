package co.edu.sena.gymtrack.service;

import co.edu.sena.gymtrack.service.dto.ScheduleDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.edu.sena.gymtrack.domain.Schedule}.
 */
public interface ScheduleService {
    /**
     * Save a schedule.
     *
     * @param scheduleDTO the entity to save.
     * @return the persisted entity.
     */
    ScheduleDTO save(ScheduleDTO scheduleDTO);

    /**
     * Updates a schedule.
     *
     * @param scheduleDTO the entity to update.
     * @return the persisted entity.
     */
    ScheduleDTO update(ScheduleDTO scheduleDTO);

    /**
     * Partially updates a schedule.
     *
     * @param scheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO);

    /**
     * Get all the schedules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScheduleDTO> findAll(Pageable pageable);

    /**
     * Get all the schedules with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScheduleDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" schedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScheduleDTO> findOne(Long id);

    /**
     * Delete the "id" schedule.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
