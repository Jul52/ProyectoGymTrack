package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.Schedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    default Optional<Schedule> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Schedule> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Schedule> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select schedule from Schedule schedule left join fetch schedule.course",
        countQuery = "select count(schedule) from Schedule schedule"
    )
    Page<Schedule> findAllWithToOneRelationships(Pageable pageable);

    @Query("select schedule from Schedule schedule left join fetch schedule.course")
    List<Schedule> findAllWithToOneRelationships();

    @Query("select schedule from Schedule schedule left join fetch schedule.course where schedule.id =:id")
    Optional<Schedule> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select s from Schedule s left join fetch s.course c " +
        "join c.gymServices gs " +
        "where gs.id = :serviceId and s.availableSlots > 0"
    )
    List<Schedule> findAvailableSchedulesByService(@Param("serviceId") Long serviceId);
}
