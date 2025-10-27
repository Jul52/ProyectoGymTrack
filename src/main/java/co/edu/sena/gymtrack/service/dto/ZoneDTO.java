package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.Zone} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ZoneDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private Boolean status;

    @NotNull
    private Set<CourseDTO> courses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Set<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseDTO> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZoneDTO)) {
            return false;
        }

        ZoneDTO zoneDTO = (ZoneDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, zoneDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ZoneDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            ", courses=" + getCourses() +
            "}";
    }
}
