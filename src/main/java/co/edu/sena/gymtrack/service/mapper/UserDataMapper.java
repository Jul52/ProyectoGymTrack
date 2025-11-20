package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.DocumentType;
import co.edu.sena.gymtrack.domain.User;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.DocumentTypeDTO;
import co.edu.sena.gymtrack.service.dto.UserDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserData} and its DTO {@link UserDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserDataMapper extends EntityMapper<UserDataDTO, UserData> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "documentType", source = "documentType", qualifiedByName = "documentTypeName")
    UserDataDTO toDto(UserData s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("documentTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DocumentTypeDTO toDtoDocumentTypeName(DocumentType documentType);
}
