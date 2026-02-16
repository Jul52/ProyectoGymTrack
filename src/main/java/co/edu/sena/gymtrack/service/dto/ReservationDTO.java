package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

public class ReservationDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean status;

    @NotNull
    private ScheduleDTO schedule;

    @NotNull
    private GymServiceDTO gymService;

    private CourseDTO course;

    // NUEVO: usuario que registró la reserva
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

    public GymServiceDTO getGymService() {
        return gymService;
    }

    public void setGymService(GymServiceDTO gymService) {
        this.gymService = gymService;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public UserDataDTO getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(UserDataDTO registeredBy) {
        this.registeredBy = registeredBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationDTO)) return false;
        ReservationDTO that = (ReservationDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "ReservationDTO{" +
            "id=" +
            getId() +
            ", status=" +
            getStatus() +
            ", description='" +
            "'" +
            ", schedule=" +
            getSchedule() +
            ", gymService=" +
            getGymService() +
            ", course=" +
            getCourse() +
            ", registeredBy=" +
            getRegisteredBy() +
            "}"
        );
    }
}
