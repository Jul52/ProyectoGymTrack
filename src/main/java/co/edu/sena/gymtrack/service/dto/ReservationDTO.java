package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.Reservation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservationDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean status;

    @NotNull
    private ScheduleDTO schedule;

    @NotNull
    private CourseDTO course;

    @NotNull
    private GymServiceDTO gymService;

    @NotNull
    private UserDataDTO registeredBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ScheduleDTO getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDTO schedule) {
        this.schedule = schedule;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public GymServiceDTO getGymService() {
        return gymService;
    }

    public void setGymService(GymServiceDTO gymService) {
        this.gymService = gymService;
    }

    public UserDataDTO getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(UserDataDTO registeredBy) {
        this.registeredBy = registeredBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservationDTO)) {
            return false;
        }

        ReservationDTO reservationDTO = (ReservationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reservationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", schedule=" + getSchedule() +
            ", course=" + getCourse() +
            ", gymService=" + getGymService() +
            ", registeredBy=" + getRegisteredBy() +
            "}";
    }
}
