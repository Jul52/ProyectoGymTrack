package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Course;
import co.edu.sena.gymtrack.domain.Schedule;
import co.edu.sena.gymtrack.service.dto.CourseDTO;
import co.edu.sena.gymtrack.service.dto.ScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseCourseName")
    @Mapping(target = "availableSlots", source = "availableSlots")
    ScheduleDTO toDto(Schedule s);

    @Named("courseCourseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "courseName", source = "courseName")
    CourseDTO toDtoCourseCourseName(Course course);
}
