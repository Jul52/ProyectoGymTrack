package co.edu.sena.gymtrack.repository;

import co.edu.sena.gymtrack.domain.UserData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findOneByUserLogin(String login);

    default Optional<UserData> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserData> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserData> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

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
}
