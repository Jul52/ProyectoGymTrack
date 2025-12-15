package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Invoice;
import co.edu.sena.gymtrack.domain.InvoiceService;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.dto.InvoiceDTO;
import co.edu.sena.gymtrack.service.dto.InvoiceServiceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InvoiceService} and its DTO {@link InvoiceServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceServiceMapper extends EntityMapper<InvoiceServiceDTO, InvoiceService> {
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceId")
    @Mapping(target = "service", source = "service", qualifiedByName = "gymServiceId")
    InvoiceServiceDTO toDto(InvoiceService s);

    @Named("invoiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceDTO toDtoInvoiceId(Invoice invoice);

    @Named("gymServiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GymServiceDTO toDtoGymServiceId(GymService gymService);
}
