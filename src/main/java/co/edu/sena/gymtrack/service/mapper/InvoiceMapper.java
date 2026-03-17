package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Invoice;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.InvoiceDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "userData", source = "userData", qualifiedByName = "userDataDocument")
    InvoiceDTO toDto(Invoice s);

    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "documentNumber", source = "documentNumber") // ✅ CORREGIDO
    UserDataDTO toDtoUserDataDocument(UserData userData);
}
