package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.MachineIncidents;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MachineIncidents entity.
 */
@Repository
public interface MachineIncidentsRepository extends JpaRepository<MachineIncidents, Long> {
    default Optional<MachineIncidents> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MachineIncidents> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MachineIncidents> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select machineIncidents from MachineIncidents machineIncidents left join fetch machineIncidents.incident left join fetch machineIncidents.machine",
        countQuery = "select count(machineIncidents) from MachineIncidents machineIncidents"
    )
    Page<MachineIncidents> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select machineIncidents from MachineIncidents machineIncidents left join fetch machineIncidents.incident left join fetch machineIncidents.machine"
    )
    List<MachineIncidents> findAllWithToOneRelationships();

    @Query(
        "select machineIncidents from MachineIncidents machineIncidents left join fetch machineIncidents.incident left join fetch machineIncidents.machine where machineIncidents.id =:id"
    )
    Optional<MachineIncidents> findOneWithToOneRelationships(@Param("id") Long id);
}
