package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Incident;
import co.edu.sena.gymtrack.domain.Machine;
import co.edu.sena.gymtrack.domain.MachineIncidents;
import co.edu.sena.gymtrack.service.dto.IncidentDTO;
import co.edu.sena.gymtrack.service.dto.MachineDTO;
import co.edu.sena.gymtrack.service.dto.MachineIncidentsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MachineIncidents} and its DTO {@link MachineIncidentsDTO}.
 */
@Mapper(componentModel = "spring")
public interface MachineIncidentsMapper extends EntityMapper<MachineIncidentsDTO, MachineIncidents> {
    @Mapping(target = "incident", source = "incident", qualifiedByName = "incidentIncidentType")
    @Mapping(target = "machine", source = "machine", qualifiedByName = "machineDescription")
    MachineIncidentsDTO toDto(MachineIncidents s);

    @Named("incidentIncidentType")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "incidentType", source = "incidentType")
    IncidentDTO toDtoIncidentIncidentType(Incident incident);

    @Named("machineDescription")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    MachineDTO toDtoMachineDescription(Machine machine);
}
