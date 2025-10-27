package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Machine.
 */
@Entity
@Table(name = "machine")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Machine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "serial", length = 50, nullable = false)
    private String serial;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "machine")
    @JsonIgnoreProperties(value = { "incident", "machine" }, allowSetters = true)
    private Set<MachineIncidents> machineIncidents = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "reservations", "machines", "invoices", "courses", "payments", "documentType" },
        allowSetters = true
    )
    private UserData admin;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Machine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return this.serial;
    }

    public Machine serial(String serial) {
        this.setSerial(serial);
        return this;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDescription() {
        return this.description;
    }

    public Machine description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Machine status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Set<MachineIncidents> getMachineIncidents() {
        return this.machineIncidents;
    }

    public void setMachineIncidents(Set<MachineIncidents> machineIncidents) {
        if (this.machineIncidents != null) {
            this.machineIncidents.forEach(i -> i.setMachine(null));
        }
        if (machineIncidents != null) {
            machineIncidents.forEach(i -> i.setMachine(this));
        }
        this.machineIncidents = machineIncidents;
    }

    public Machine machineIncidents(Set<MachineIncidents> machineIncidents) {
        this.setMachineIncidents(machineIncidents);
        return this;
    }

    public Machine addMachineIncidents(MachineIncidents machineIncidents) {
        this.machineIncidents.add(machineIncidents);
        machineIncidents.setMachine(this);
        return this;
    }

    public Machine removeMachineIncidents(MachineIncidents machineIncidents) {
        this.machineIncidents.remove(machineIncidents);
        machineIncidents.setMachine(null);
        return this;
    }

    public UserData getAdmin() {
        return this.admin;
    }

    public void setAdmin(UserData userData) {
        this.admin = userData;
    }

    public Machine admin(UserData userData) {
        this.setAdmin(userData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Machine)) {
            return false;
        }
        return getId() != null && getId().equals(((Machine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Machine{" +
            "id=" + getId() +
            ", serial='" + getSerial() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
