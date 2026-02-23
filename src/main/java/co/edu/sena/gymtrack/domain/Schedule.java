package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Schedule.
 */
@Entity
@Table(name = "schedule")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "day_of_week", length = 20, nullable = false)
    private String dayOfWeek;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedules", "zones", "trainer", "reservations" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Schedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }

    public Schedule dayOfWeek(String dayOfWeek) {
        this.setDayOfWeek(dayOfWeek);
        return this;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Schedule startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Schedule endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Schedule course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Integer getAvailableSlots() {
        return this.availableSlots;
    }

    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }

    public Schedule availableSlots(Integer availableSlots) {
        this.setAvailableSlots(availableSlots);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return getId() != null && getId().equals(((Schedule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + getId() +
            ", dayOfWeek='" + getDayOfWeek() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
