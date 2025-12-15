package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.UserData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserData entity.
 */
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    // Método que necesitas para el servicio PaymentServiceImpl (convención de Spring Data)
    // Busca UserData navegando por la propiedad 'user' y filtrando por su 'login'.
    Optional<UserData> findOneByUserLogin(String login);

    // --------------- Métodos de JHipster (Sin cambios) -----------------------

    default Optional<UserData> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserData> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserData> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    // Tu método findByUserLogin original (usa HQL, es redundante con findOneByUserLogin)
    // Puedes dejarlo si otros lugares lo usan, pero lo ideal es usar la convención de Spring Data.
    // Lo dejo por ahora, ya que no falla la inicialización.
    @Query("select userData from UserData userData left join fetch userData.user where userData.user.login = ?1")
    Optional<UserData> findByUserLogin(String login);

    @Query(
        value = "select userData from UserData userData left join fetch userData.user left join fetch userData.documentType",
        countQuery = "select count(userData) from UserData userData"
    )
    Page<UserData> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userData from UserData userData left join fetch userData.user left join fetch userData.documentType")
    List<UserData> findAllWithToOneRelationships();

    @Query(
        "select userData from UserData userData left join fetch userData.user left join fetch userData.documentType where userData.id =:id"
    )
    Optional<UserData> findOneWithToOneRelationships(@Param("id") Long id);
    // ESTA LÍNEA CAUSÓ EL ERROR DE INICIO. HAY QUE ELIMINARLA O COMENTARLA.
    // No property 'login' found for type 'UserData'
    // Optional<UserData> findOneByLogin(String userLogin);
}
