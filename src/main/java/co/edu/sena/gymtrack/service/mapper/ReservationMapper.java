package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Course;
import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Reservation;
import co.edu.sena.gymtrack.domain.Schedule;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.CourseDTO;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.dto.ScheduleDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reservation} and its DTO {@link ReservationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseCourseName")
    @Mapping(target = "gymService", source = "gymService", qualifiedByName = "gymServiceServiceName")
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "scheduleId")
    @Mapping(target = "registeredBy", source = "registeredBy", qualifiedByName = "userDataDocument")
    ReservationDTO toDto(Reservation s);

    @Named("courseCourseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "courseName", source = "courseName")
    CourseDTO toDtoCourseCourseName(Course course);

    @Named("gymServiceServiceName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "serviceName", source = "serviceName")
    GymServiceDTO toDtoGymServiceServiceName(GymService gymService);

    @Named("scheduleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek") // FIX: campo faltante
    @Mapping(target = "startTime", source = "startTime") // FIX: campo faltante
    @Mapping(target = "endTime", source = "endTime") // FIX: campo faltante
    ScheduleDTO toDtoScheduleId(Schedule schedule);

    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "document", source = "document")
    UserDataDTO toDtoUserDataDocument(UserData userData);
}
