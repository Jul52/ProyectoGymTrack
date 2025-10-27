package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Incident;
import co.edu.sena.gymtrack.service.dto.IncidentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Incident} and its DTO {@link IncidentDTO}.
 */
@Mapper(componentModel = "spring")
public interface IncidentMapper extends EntityMapper<IncidentDTO, Incident> {}
