package co.edu.sena.gymtrack.service;

import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.edu.sena.gymtrack.domain.GymService}.
 */
public interface GymServiceService {
    /**
     * Save a gymService.
     *
     * @param gymServiceDTO the entity to save.
     * @return the persisted entity.
     */
    GymServiceDTO save(GymServiceDTO gymServiceDTO);

    /**
     * Updates a gymService.
     *
     * @param gymServiceDTO the entity to update.
     * @return the persisted entity.
     */
    GymServiceDTO update(GymServiceDTO gymServiceDTO);

    /**
     * Partially updates a gymService.
     *
     * @param gymServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GymServiceDTO> partialUpdate(GymServiceDTO gymServiceDTO);

    /**
     * Get all the gymServices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GymServiceDTO> findAll(Pageable pageable);

    /**
     * Get all the gymServices with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GymServiceDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" gymService.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GymServiceDTO> findOne(Long id);

    /**
     * Delete the "id" gymService.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
