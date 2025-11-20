package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.InvoiceService;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InvoiceService entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceServiceRepository extends JpaRepository<InvoiceService, Long> {}
