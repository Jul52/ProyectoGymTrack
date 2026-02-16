package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.*;
import co.edu.sena.gymtrack.service.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseCourseName")
    @Mapping(target = "gymService", source = "gymService", qualifiedByName = "gymServiceServiceName")
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "scheduleBasic")
    ReservationDTO toDto(Reservation s);

    @Mapping(target = "registeredBy", ignore = true) // Se asigna en el Service
    @Mapping(target = "course", ignore = true) // Se toma desde el schedule en el Service
    Reservation toEntity(ReservationDTO reservationDTO);

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

    @Named("scheduleBasic")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "availableSlots", source = "availableSlots")
    ScheduleDTO toDtoScheduleBasic(Schedule schedule);
}
