package co.edu.sena.gymtrack.service;

import co.edu.sena.gymtrack.service.dto.MachineIncidentsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.edu.sena.gymtrack.domain.MachineIncidents}.
 */
public interface MachineIncidentsService {
    /**
     * Save a machineIncidents.
     *
     * @param machineIncidentsDTO the entity to save.
     * @return the persisted entity.
     */
    MachineIncidentsDTO save(MachineIncidentsDTO machineIncidentsDTO);

    /**
     * Updates a machineIncidents.
     *
     * @param machineIncidentsDTO the entity to update.
     * @return the persisted entity.
     */
    MachineIncidentsDTO update(MachineIncidentsDTO machineIncidentsDTO);

    /**
     * Partially updates a machineIncidents.
     *
     * @param machineIncidentsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MachineIncidentsDTO> partialUpdate(MachineIncidentsDTO machineIncidentsDTO);

    /**
     * Get all the machineIncidents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MachineIncidentsDTO> findAll(Pageable pageable);

    /**
     * Get all the machineIncidents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MachineIncidentsDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" machineIncidents.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MachineIncidentsDTO> findOne(Long id);

    /**
     * Delete the "id" machineIncidents.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
