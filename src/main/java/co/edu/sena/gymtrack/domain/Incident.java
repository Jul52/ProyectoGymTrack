package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Incident.
 */
@Entity
@Table(name = "incident")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Incident implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "incident_type", length = 50, nullable = false)
    private String incidentType;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "reported_date")
    private Instant reportedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "incident")
    @JsonIgnoreProperties(value = { "incident", "machine" }, allowSetters = true)
    private Set<MachineIncidents> machineIncidents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Incident id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIncidentType() {
        return this.incidentType;
    }

    public Incident incidentType(String incidentType) {
        this.setIncidentType(incidentType);
        return this;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getDescription() {
        return this.description;
    }

    public Incident description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getReportedDate() {
        return this.reportedDate;
    }

    public Incident reportedDate(Instant reportedDate) {
        this.setReportedDate(reportedDate);
        return this;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    public Set<MachineIncidents> getMachineIncidents() {
        return this.machineIncidents;
    }

    public void setMachineIncidents(Set<MachineIncidents> machineIncidents) {
        if (this.machineIncidents != null) {
            this.machineIncidents.forEach(i -> i.setIncident(null));
        }
        if (machineIncidents != null) {
            machineIncidents.forEach(i -> i.setIncident(this));
        }
        this.machineIncidents = machineIncidents;
    }

    public Incident machineIncidents(Set<MachineIncidents> machineIncidents) {
        this.setMachineIncidents(machineIncidents);
        return this;
    }

    public Incident addMachineIncidents(MachineIncidents machineIncidents) {
        this.machineIncidents.add(machineIncidents);
        machineIncidents.setIncident(this);
        return this;
    }

    public Incident removeMachineIncidents(MachineIncidents machineIncidents) {
        this.machineIncidents.remove(machineIncidents);
        machineIncidents.setIncident(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Incident)) {
            return false;
        }
        return getId() != null && getId().equals(((Incident) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Incident{" +
            "id=" + getId() +
            ", incidentType='" + getIncidentType() + "'" +
            ", description='" + getDescription() + "'" +
            ", reportedDate='" + getReportedDate() + "'" +
            "}";
    }
}
