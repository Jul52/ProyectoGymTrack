package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Payment;
import co.edu.sena.gymtrack.domain.PaymentMethod;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.InvoiceRepository;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.dto.PaymentMethodDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper implements EntityMapper<PaymentDTO, Payment> {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodMethodName")
    @Mapping(target = "registeredBy", source = "registeredBy", qualifiedByName = "userDataDocumentType")
    @Mapping(target = "serviceName", ignore = true)
    public abstract PaymentDTO toDto(Payment s);

    @AfterMapping
    public void fillServiceName(Payment payment, @MappingTarget PaymentDTO dto) {
        if (payment.getId() == null) return;
        System.out.println(">>> fillServiceName llamado para payment.id=" + payment.getId());
        invoiceRepository
            .findByPaymentId(payment.getId())
            .ifPresentOrElse(
                invoice -> {
                    System.out.println(">>> Invoice encontrada: " + invoice.getId() + ", service: " + invoice.getService());
                    if (invoice.getService() != null) {
                        dto.setServiceName(invoice.getService().getServiceName());
                    }
                },
                () -> System.out.println(">>> No se encontró invoice para payment.id=" + payment.getId())
            );
    }

    @Named("paymentMethodMethodName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "methodName", source = "methodName")
    public abstract PaymentMethodDTO toDtoPaymentMethodMethodName(PaymentMethod paymentMethod);

    @Named("userDataDocumentType")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "documentNumber", source = "documentNumber")
    public abstract UserDataDTO toDtoUserDataDocumentType(UserData userData);
}
