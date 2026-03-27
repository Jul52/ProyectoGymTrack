package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Invoice;
import co.edu.sena.gymtrack.domain.Payment;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.dto.InvoiceDTO;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "userData", source = "userData", qualifiedByName = "userDataDocument")
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    @Mapping(target = "service", source = "service", qualifiedByName = "serviceBasic")
    InvoiceDTO toDto(Invoice s);

    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "documentNumber", source = "documentNumber")
    UserDataDTO toDtoUserDataDocument(UserData userData);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("serviceBasic")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "serviceName", source = "serviceName")
    @Mapping(target = "price", source = "price")
    GymServiceDTO toDtoServiceBasic(GymService gymService);
}
