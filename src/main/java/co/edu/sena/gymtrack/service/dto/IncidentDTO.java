package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.Incident} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String incidentType;

    @Size(max = 255)
    private String description;

    private Instant reportedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncidentDTO)) {
            return false;
        }

        IncidentDTO incidentDTO = (IncidentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, incidentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidentDTO{" +
            "id=" + getId() +
            ", incidentType='" + getIncidentType() + "'" +
            ", description='" + getDescription() + "'" +
            ", reportedDate='" + getReportedDate() + "'" +
            "}";
    }
}
