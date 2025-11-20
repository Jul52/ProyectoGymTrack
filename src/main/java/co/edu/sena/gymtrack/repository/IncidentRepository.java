package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.Incident;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Incident entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {}
