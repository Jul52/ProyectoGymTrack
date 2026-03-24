package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    default Optional<Reservation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reservation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reservation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reservation from Reservation reservation " +
        "left join fetch reservation.course " +
        "left join fetch reservation.gymService " +
        "left join fetch reservation.registeredBy " +
        "left join fetch reservation.schedule",
        countQuery = "select count(reservation) from Reservation reservation"
    )
    Page<Reservation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select reservation from Reservation reservation " +
        "left join fetch reservation.course " +
        "left join fetch reservation.gymService " +
        "left join fetch reservation.registeredBy " +
        "left join fetch reservation.schedule"
    )
    List<Reservation> findAllWithToOneRelationships();

    @Query(
        "select reservation from Reservation reservation " +
        "left join fetch reservation.course " +
        "left join fetch reservation.gymService " +
        "left join fetch reservation.registeredBy " +
        "left join fetch reservation.schedule " +
        "where reservation.id = :id"
    )
    Optional<Reservation> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "select reservation from Reservation reservation " +
        "left join fetch reservation.course " +
        "left join fetch reservation.gymService " +
        "left join fetch reservation.registeredBy " +
        "left join fetch reservation.schedule " +
        "where reservation.registeredBy.user.login = :login",
        countQuery = "select count(reservation) from Reservation reservation " + "where reservation.registeredBy.user.login = :login"
    )
    Page<Reservation> findAllByUserLogin(@Param("login") String login, Pageable pageable);

    @Query(
        """
        select count(r) from Reservation r
        where r.registeredBy.id = :userId
        and r.gymService.id = :serviceId
        and r.course.id = :courseId
        and r.status = true
        """
    )
    long countActiveReservationsByUserServiceAndCourse(
        @Param("userId") Long userId,
        @Param("serviceId") Long serviceId,
        @Param("courseId") Long courseId
    );

    boolean existsByRegisteredByIdAndScheduleId(Long registeredById, Long scheduleId);
}
