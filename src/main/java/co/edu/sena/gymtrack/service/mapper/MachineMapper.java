package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Machine;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.MachineDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Machine} and its DTO {@link MachineDTO}.
 */
@Mapper(componentModel = "spring")
public interface MachineMapper extends EntityMapper<MachineDTO, Machine> {
    @Mapping(target = "admin", source = "admin", qualifiedByName = "userDataDocument")
    MachineDTO toDto(Machine s);

    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "document", source = "document")
    UserDataDTO toDtoUserDataDocument(UserData userData);
}
