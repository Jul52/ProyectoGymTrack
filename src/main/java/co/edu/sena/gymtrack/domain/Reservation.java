package co.edu.sena.gymtrack.domain;

import co.edu.sena.gymtrack.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Reservation.
 */
@Entity
@Table(name = "reservation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private Schedule schedule;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedules", "zones", "trainer", "reservations" }, allowSetters = true)
    private Course course;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "invoiceServices", "category", "reservations" }, allowSetters = true)
    private GymService gymService;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "reservations", "machines", "invoices", "courses", "payments", "documentType" },
        allowSetters = true
    )
    @JoinColumn(name = "registered_by_id")
    private UserData registeredBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reservation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Reservation status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Reservation schedule(Schedule schedule) {
        this.setSchedule(schedule);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Reservation course(Course course) {
        this.setCourse(course);
        return this;
    }

    public GymService getGymService() {
        return this.gymService;
    }

    public void setGymService(GymService gymService) {
        this.gymService = gymService;
    }

    public Reservation gymService(GymService gymService) {
        this.setGymService(gymService);
        return this;
    }

    public UserData getRegisteredBy() { // CAMBIO: Getter para registeredBy
        return this.registeredBy;
    }

    public void setRegisteredBy(UserData registeredBy) { // CAMBIO: Setter para registeredBy
        this.registeredBy = registeredBy;
    }

    public Reservation registeredBy(UserData registeredBy) { // CAMBIO: Fluent setter para registeredBy
        this.setRegisteredBy(registeredBy);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        return getId() != null && getId().equals(((Reservation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", schedule='" + getSchedule() + "'" +
            "}";
    }
}
