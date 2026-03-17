package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Payment;
import co.edu.sena.gymtrack.domain.PaymentMethod;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.dto.PaymentMethodDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodMethodName")
    @Mapping(target = "registeredBy", source = "registeredBy", qualifiedByName = "userDataDocumentType")
    PaymentDTO toDto(Payment s);

    @Named("paymentMethodMethodName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "methodName", source = "methodName")
    PaymentMethodDTO toDtoPaymentMethodMethodName(PaymentMethod paymentMethod);

    @Named("userDataDocumentType")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "documentType", source = "documentType") // ✅ OK si UserDataDTO tiene este campo
    @Mapping(target = "documentNumber", source = "documentNumber") // ✅ Agrega este si también lo necesitas en Payment
    UserDataDTO toDtoUserDataDocumentType(UserData userData);
}
