package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Course;
import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.domain.Reservation;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.service.dto.CourseDTO;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.dto.ReservationDTO;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reservation} and its DTO {@link ReservationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    // toDto: Entidad -> DTO (Lee de registeredBy, escribe a userData)
    @Mapping(target = "course", source = "course", qualifiedByName = "courseCourseName")
    @Mapping(target = "gymService", source = "gymService", qualifiedByName = "gymServiceServiceName")
    @Mapping(target = "userData", source = "registeredBy", qualifiedByName = "userDataDocument")
    ReservationDTO toDto(Reservation s);

    // toEntity: DTO -> Entidad (Lee de userData, escribe a registeredBy)
    @Mapping(target = "registeredBy", source = "userData")
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

    // Este QualifiedByName puede permanecer igual, solo necesita el id y el document
    // (Asegúrate de que UserDataMapper tenga un método con @Named("userDataDocument")
    @Named("userDataDocument")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "document", source = "document")
    UserDataDTO toDtoUserDataDocument(UserData userData);
}
