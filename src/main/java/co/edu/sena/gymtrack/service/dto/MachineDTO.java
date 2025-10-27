package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.Machine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MachineDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String serial;

    @NotNull
    @Size(max = 255)
    private String description;

    @NotNull
    private Boolean status;

    @NotNull
    private UserDataDTO admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public UserDataDTO getAdmin() {
        return admin;
    }

    public void setAdmin(UserDataDTO admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MachineDTO)) {
            return false;
        }

        MachineDTO machineDTO = (MachineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, machineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MachineDTO{" +
            "id=" + getId() +
            ", serial='" + getSerial() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", admin=" + getAdmin() +
            "}";
    }
}
