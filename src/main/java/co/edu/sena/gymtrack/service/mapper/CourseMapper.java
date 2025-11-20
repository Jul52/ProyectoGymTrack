package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Course;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.domain.Zone;
import co.edu.sena.gymtrack.service.dto.CourseDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import co.edu.sena.gymtrack.service.dto.ZoneDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "zones", source = "zones", qualifiedByName = "zoneNameSet")
    @Mapping(target = "trainer", source = "trainer", qualifiedByName = "userDataId")
    CourseDTO toDto(Course s);

    @Mapping(target = "removeZone", ignore = true)
    Course toEntity(CourseDTO courseDTO);

    @Named("zoneName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ZoneDTO toDtoZoneName(Zone zone);

    @Named("zoneNameSet")
    default Set<ZoneDTO> toDtoZoneNameSet(Set<Zone> zone) {
        return zone.stream().map(this::toDtoZoneName).collect(Collectors.toSet());
    }

    @Named("userDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDataDTO toDtoUserDataId(UserData userData);
}
