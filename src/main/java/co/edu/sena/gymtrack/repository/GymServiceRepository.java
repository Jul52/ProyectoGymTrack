package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.GymService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GymService entity.
 */
@Repository
public interface GymServiceRepository extends JpaRepository<GymService, Long> {
    default Optional<GymService> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<GymService> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<GymService> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select gymService from GymService gymService left join fetch gymService.category",
        countQuery = "select count(gymService) from GymService gymService"
    )
    Page<GymService> findAllWithToOneRelationships(Pageable pageable);

    @Query("select gymService from GymService gymService left join fetch gymService.category")
    List<GymService> findAllWithToOneRelationships();

    @Query("select gymService from GymService gymService left join fetch gymService.category where gymService.id =:id")
    Optional<GymService> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select distinct is.service from InvoiceService is " +
        "where is.invoice.userData.user.login = :login " +
        "and is.service.status = true"
    )
    List<GymService> findServicesByUserLogin(@Param("login") String login);
}
