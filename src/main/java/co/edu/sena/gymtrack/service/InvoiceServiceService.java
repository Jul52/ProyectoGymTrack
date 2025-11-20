package co.edu.sena.gymtrack.service;

import co.edu.sena.gymtrack.service.dto.InvoiceServiceDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.edu.sena.gymtrack.domain.InvoiceService}.
 */
public interface InvoiceServiceService {
    /**
     * Save a invoiceService.
     *
     * @param invoiceServiceDTO the entity to save.
     * @return the persisted entity.
     */
    InvoiceServiceDTO save(InvoiceServiceDTO invoiceServiceDTO);

    /**
     * Updates a invoiceService.
     *
     * @param invoiceServiceDTO the entity to update.
     * @return the persisted entity.
     */
    InvoiceServiceDTO update(InvoiceServiceDTO invoiceServiceDTO);

    /**
     * Partially updates a invoiceService.
     *
     * @param invoiceServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InvoiceServiceDTO> partialUpdate(InvoiceServiceDTO invoiceServiceDTO);

    /**
     * Get all the invoiceServices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InvoiceServiceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" invoiceService.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InvoiceServiceDTO> findOne(Long id);

    /**
     * Delete the "id" invoiceService.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
