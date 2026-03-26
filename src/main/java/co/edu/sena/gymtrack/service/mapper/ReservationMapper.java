package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Reservation;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reservation} and its DTO {@link ReservationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    @Mapping(target = "registeredBy", source = "registeredBy", qualifiedByName = "userDataDocument")
    ReservationDTO toDto(Reservation s);

    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "documentNumber", source = "documentNumber") // ✅ target debe coincidir con el campo en UserDataDTO
    UserDataDTO toDtoUserDataDocument(UserData userData);
}
