package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.Reservation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservationDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean status;

    @Size(max = 255)
    private String description;

    @NotNull
    private LocalDate reservationDate;

    @NotNull
    private CourseDTO course;

    @NotNull
    private GymServiceDTO gymService;

    @NotNull
    private UserDataDTO userData;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
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

    public UserDataDTO getUserData() {
        return userData;
    }

    public void setUserData(UserDataDTO userData) {
        this.userData = userData;
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
            ", description='" + getDescription() + "'" +
            ", reservationDate='" + getReservationDate() + "'" +
            ", course=" + getCourse() +
            ", gymService=" + getGymService() +
            ", userData=" + getUserData() +
            "}";
    }
}
