package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Invoice;
import co.edu.sena.gymtrack.domain.Payment;
import co.edu.sena.gymtrack.domain.PaymentMethod;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.InvoiceDTO;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.dto.PaymentMethodDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodMethodName")
    @Mapping(target = "userData", source = "userData", qualifiedByName = "userDataDocument")
    InvoiceDTO toDto(Invoice s);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "transactionId", source = "transactionId")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("paymentMethodMethodName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "methodName", source = "methodName")
    PaymentMethodDTO toDtoPaymentMethodMethodName(PaymentMethod paymentMethod);

    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "document", source = "document")
    UserDataDTO toDtoUserDataDocument(UserData userData);
}
