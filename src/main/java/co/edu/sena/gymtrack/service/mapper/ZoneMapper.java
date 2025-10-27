package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Course;
import co.edu.sena.gymtrack.domain.Zone;
import co.edu.sena.gymtrack.service.dto.CourseDTO;
import co.edu.sena.gymtrack.service.dto.ZoneDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Zone} and its DTO {@link ZoneDTO}.
 */
@Mapper(componentModel = "spring")
public interface ZoneMapper extends EntityMapper<ZoneDTO, Zone> {
    @Mapping(target = "courses", source = "courses", qualifiedByName = "courseCourseNameSet")
    ZoneDTO toDto(Zone s);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "removeCourse", ignore = true)
    Zone toEntity(ZoneDTO zoneDTO);

    @Named("courseCourseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "courseName", source = "courseName")
    CourseDTO toDtoCourseCourseName(Course course);

    @Named("courseCourseNameSet")
    default Set<CourseDTO> toDtoCourseCourseNameSet(Set<Course> course) {
        return course.stream().map(this::toDtoCourseCourseName).collect(Collectors.toSet());
    }
}
