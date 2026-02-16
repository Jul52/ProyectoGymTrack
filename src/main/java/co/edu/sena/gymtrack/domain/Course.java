package co.edu.sena.gymtrack.domain;

import co.edu.sena.gymtrack.domain.GymService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "course_name", length = 100, nullable = false)
    private String courseName;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "capacity")
    private Integer capacity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private Set<Schedule> schedules = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull
    @JoinTable(name = "rel_course__zone", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "zone_id"))
    @JsonIgnoreProperties(value = { "courses" }, allowSetters = true)
    private Set<Zone> zones = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "reservations", "machines", "invoices", "courses", "payments", "documentType" },
        allowSetters = true
    )
    private UserData trainer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @JsonIgnoreProperties(value = { "course", "gymService", "userData" }, allowSetters = true)
    private Set<Reservation> reservations = new HashSet<>();

    @ManyToMany(mappedBy = "courses")
    @JsonIgnoreProperties(value = { "invoiceServices", "category", "reservations", "courses" }, allowSetters = true)
    private Set<GymService> gymServices = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Course id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public Course courseName(String courseName) {
        this.setCourseName(courseName);
        return this;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Course status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Course startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Course endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Course capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Set<Schedule> getSchedules() {
        return this.schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        if (this.schedules != null) {
            this.schedules.forEach(i -> i.setCourse(null));
        }
        if (schedules != null) {
            schedules.forEach(i -> i.setCourse(this));
        }
        this.schedules = schedules;
    }

    public Course schedules(Set<Schedule> schedules) {
        this.setSchedules(schedules);
        return this;
    }

    public Course addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setCourse(this);
        return this;
    }

    public Course removeSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
        schedule.setCourse(null);
        return this;
    }

    public Set<Zone> getZones() {
        return this.zones;
    }

    public void setZones(Set<Zone> zones) {
        this.zones = zones;
    }

    public Course zones(Set<Zone> zones) {
        this.setZones(zones);
        return this;
    }

    public Course addZone(Zone zone) {
        this.zones.add(zone);
        return this;
    }

    public Course removeZone(Zone zone) {
        this.zones.remove(zone);
        return this;
    }

    public UserData getTrainer() {
        return this.trainer;
    }

    public void setTrainer(UserData userData) {
        this.trainer = userData;
    }

    public Course trainer(UserData userData) {
        this.setTrainer(userData);
        return this;
    }

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(i -> i.setCourse(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setCourse(this));
        }
        this.reservations = reservations;
    }

    public Course reservations(Set<Reservation> reservations) {
        this.setReservations(reservations);
        return this;
    }

    public Course addReservations(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setCourse(this);
        return this;
    }

    public Course removeReservations(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setCourse(null);
        return this;
    }

    public Set<GymService> getGymServices() {
        return this.gymServices;
    }

    public void setGymServices(Set<GymService> gymServices) {
        this.gymServices = gymServices;
    }

    public Course gymServices(Set<GymService> gymServices) {
        this.setGymServices(gymServices);
        return this;
    }

    public Course addGymService(GymService gymService) {
        this.gymServices.add(gymService);
        return this;
    }

    public Course removeGymService(GymService gymService) {
        this.gymServices.remove(gymService);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return getId() != null && getId().equals(((Course) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", courseName='" + getCourseName() + "'" +
            ", status='" + getStatus() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", capacity=" + getCapacity() +
            "}";
    }
}
