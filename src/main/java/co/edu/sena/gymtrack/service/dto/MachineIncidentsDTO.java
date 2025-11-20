package co.edu.sena.gymtrack.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.MachineIncidents} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MachineIncidentsDTO implements Serializable {

    private Long id;

    @Size(max = 255)
    private String description;

    @Lob
    private byte[] image;

    private String imageContentType;

    @Size(max = 255)
    private String video;

    @NotNull
    private IncidentDTO incident;

    @NotNull
    private MachineDTO machine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public IncidentDTO getIncident() {
        return incident;
    }

    public void setIncident(IncidentDTO incident) {
        this.incident = incident;
    }

    public MachineDTO getMachine() {
        return machine;
    }

    public void setMachine(MachineDTO machine) {
        this.machine = machine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MachineIncidentsDTO)) {
            return false;
        }

        MachineIncidentsDTO machineIncidentsDTO = (MachineIncidentsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, machineIncidentsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MachineIncidentsDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", video='" + getVideo() + "'" +
            ", incident=" + getIncident() +
            ", machine=" + getMachine() +
            "}";
    }
}
