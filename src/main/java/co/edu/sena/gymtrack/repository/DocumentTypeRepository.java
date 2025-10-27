package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.DocumentType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DocumentType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {}
