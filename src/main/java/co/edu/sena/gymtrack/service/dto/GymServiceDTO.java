package co.edu.sena.gymtrack.service.dto;

import co.edu.sena.gymtrack.domain.enumeration.CourseAccessType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.GymService} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GymServiceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String serviceName;

    @Size(max = 255)
    private String serviceDescription;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Boolean status;

    // 🔥 NUEVOS CAMPOS
    @NotNull
    private CourseAccessType courseAccessType;

    private Integer maxReservationsPerCourse;

    @NotNull
    private CategoryDTO category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public CourseAccessType getCourseAccessType() {
        return courseAccessType;
    }

    public void setCourseAccessType(CourseAccessType courseAccessType) {
        this.courseAccessType = courseAccessType;
    }

    public Integer getMaxReservationsPerCourse() {
        return maxReservationsPerCourse;
    }

    public void setMaxReservationsPerCourse(Integer maxReservationsPerCourse) {
        this.maxReservationsPerCourse = maxReservationsPerCourse;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GymServiceDTO)) {
            return false;
        }

        GymServiceDTO gymServiceDTO = (GymServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gymServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "GymServiceDTO{" +
            "id=" +
            getId() +
            ", serviceName='" +
            getServiceName() +
            "'" +
            ", serviceDescription='" +
            getServiceDescription() +
            "'" +
            ", price=" +
            getPrice() +
            ", status=" +
            getStatus() +
            ", courseAccessType=" +
            getCourseAccessType() +
            ", maxReservationsPerCourse=" +
            getMaxReservationsPerCourse() +
            ", category=" +
            getCategory() +
            "}"
        );
    }
}
