package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.Invoice;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    default Optional<Invoice> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Invoice> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Invoice> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select invoice from Invoice invoice left join fetch invoice.paymentMethod left join fetch invoice.userData left join fetch invoice.service",
        countQuery = "select count(invoice) from Invoice invoice"
    )
    Page<Invoice> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select invoice from Invoice invoice left join fetch invoice.paymentMethod left join fetch invoice.userData left join fetch invoice.service"
    )
    List<Invoice> findAllWithToOneRelationships();

    @Query(
        "select invoice from Invoice invoice left join fetch invoice.paymentMethod left join fetch invoice.userData left join fetch invoice.service where invoice.id =:id"
    )
    Optional<Invoice> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
        SELECT is2.service.id, MAX(i.createdDate)
        FROM InvoiceService is2
        JOIN is2.invoice i
        WHERE i.userData.user.login = :login
        GROUP BY is2.service.id
        """
    )
    List<Object[]> findLatestPurchaseDateByUserLogin(@Param("login") String login);

    @Query(
        """
        SELECT i FROM Invoice i
        LEFT JOIN FETCH i.paymentMethod
        LEFT JOIN FETCH i.userData ud
        LEFT JOIN FETCH i.service
        WHERE ud.user.login = :login
        """
    )
    Page<Invoice> findAllByUserLogin(@Param("login") String login, Pageable pageable);
}
