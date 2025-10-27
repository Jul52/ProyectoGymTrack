package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.PaymentMethod;
import co.edu.sena.gymtrack.service.dto.PaymentMethodDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentMethod} and its DTO {@link PaymentMethodDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMethodMapper extends EntityMapper<PaymentMethodDTO, PaymentMethod> {}
