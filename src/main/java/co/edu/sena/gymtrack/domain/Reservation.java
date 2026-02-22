package co.edu.sena.gymtrack.domain;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "reservations" }, allowSetters = true)
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
    @JsonIgnoreProperties(value = { "user", "reservations", "machines", "invoices", "courses", "payments" }, allowSetters = true)
    @JoinColumn(name = "registered_by_id")
    private UserData registeredBy;

    // Métodos Fluidos (Necesarios para Tests y Samples)

    public Reservation id(Long id) {
        this.setId(id);
        return this;
    }

    public Reservation status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public Reservation schedule(Schedule schedule) {
        this.setSchedule(schedule);
        return this;
    }

    public Reservation course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Reservation gymService(GymService gymService) {
        this.setGymService(gymService);
        return this;
    }

    public Reservation registeredBy(UserData userData) {
        this.setRegisteredBy(userData);
        return this;
    }

    // Getters and Setters tradicionales

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return this.status;
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

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public GymService getGymService() {
        return this.gymService;
    }

    public void setGymService(GymService gymService) {
        this.gymService = gymService;
    }

    public UserData getRegisteredBy() {
        return this.registeredBy;
    }

    public void setRegisteredBy(UserData userData) {
        this.registeredBy = userData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        return getId() != null && getId().equals(((Reservation) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Reservation{" + "id=" + getId() + ", status='" + getStatus() + "'" + "}";
    }
}
