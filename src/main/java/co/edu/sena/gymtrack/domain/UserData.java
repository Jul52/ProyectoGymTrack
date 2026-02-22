package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A UserData.
 */
@Entity
@Table(name = "user_data")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Size(max = 100)
    @Column(name = "second_name", length = 100)
    private String secondName;

    @NotNull
    @Size(max = 100)
    @Column(name = "first_last_name", length = 100, nullable = false)
    private String firstLastName;

    @Size(max = 100)
    @Column(name = "second_last_name", length = 100)
    private String secondLastName;

    @NotNull
    @Size(max = 20)
    @Column(name = "document", length = 20, nullable = false)
    private String document;

    @NotNull
    @Size(max = 20)
    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userData")
    @JsonIgnoreProperties(value = { "course", "gymService", "userData" }, allowSetters = true)
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admin")
    @JsonIgnoreProperties(value = { "machineIncidents", "admin" }, allowSetters = true)
    private Set<Machine> machines = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userData")
    @JsonIgnoreProperties(value = { "payment", "invoiceServices", "paymentMethod", "userData", "service" }, allowSetters = true)
    private Set<Invoice> invoices = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trainer")
    @JsonIgnoreProperties(value = { "schedules", "zones", "trainer", "reservations" }, allowSetters = true)
    private Set<Course> courses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "registeredBy")
    @JsonIgnoreProperties(value = { "paymentMethod", "invoice", "registeredBy" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "userData" }, allowSetters = true)
    private DocumentType documentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UserData firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public UserData secondName(String secondName) {
        this.setSecondName(secondName);
        return this;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstLastName() {
        return this.firstLastName;
    }

    public UserData firstLastName(String firstLastName) {
        this.setFirstLastName(firstLastName);
        return this;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return this.secondLastName;
    }

    public UserData secondLastName(String secondLastName) {
        this.setSecondLastName(secondLastName);
        return this;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getDocument() {
        return this.document;
    }

    public UserData document(String document) {
        this.setDocument(document);
        return this;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public UserData phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public UserData birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserData user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(i -> i.setUserData(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setUserData(this));
        }
        this.reservations = reservations;
    }

    public UserData reservations(Set<Reservation> reservations) {
        this.setReservations(reservations);
        return this;
    }

    public UserData addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setUserData(this);
        return this;
    }

    public UserData removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setUserData(null);
        return this;
    }

    public Set<Machine> getMachines() {
        return this.machines;
    }

    public void setMachines(Set<Machine> machines) {
        if (this.machines != null) {
            this.machines.forEach(i -> i.setAdmin(null));
        }
        if (machines != null) {
            machines.forEach(i -> i.setAdmin(this));
        }
        this.machines = machines;
    }

    public UserData machines(Set<Machine> machines) {
        this.setMachines(machines);
        return this;
    }

    public UserData addMachine(Machine machine) {
        this.machines.add(machine);
        machine.setAdmin(this);
        return this;
    }

    public UserData removeMachine(Machine machine) {
        this.machines.remove(machine);
        machine.setAdmin(null);
        return this;
    }

    public Set<Invoice> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<Invoice> invoices) {
        if (this.invoices != null) {
            this.invoices.forEach(i -> i.setUserData(null));
        }
        if (invoices != null) {
            invoices.forEach(i -> i.setUserData(this));
        }
        this.invoices = invoices;
    }

    public UserData invoices(Set<Invoice> invoices) {
        this.setInvoices(invoices);
        return this;
    }

    public UserData addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setUserData(this);
        return this;
    }

    public UserData removeInvoice(Invoice invoice) {
        this.invoices.remove(invoice);
        invoice.setUserData(null);
        return this;
    }

    public Set<Course> getCourses() {
        return this.courses;
    }

    public void setCourses(Set<Course> courses) {
        if (this.courses != null) {
            this.courses.forEach(i -> i.setTrainer(null));
        }
        if (courses != null) {
            courses.forEach(i -> i.setTrainer(this));
        }
        this.courses = courses;
    }

    public UserData courses(Set<Course> courses) {
        this.setCourses(courses);
        return this;
    }

    public UserData addCourse(Course course) {
        this.courses.add(course);
        course.setTrainer(this);
        return this;
    }

    public UserData removeCourse(Course course) {
        this.courses.remove(course);
        course.setTrainer(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setRegisteredBy(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setRegisteredBy(this));
        }
        this.payments = payments;
    }

    public UserData payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public UserData addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setRegisteredBy(this);
        return this;
    }

    public UserData removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setRegisteredBy(null);
        return this;
    }

    public DocumentType getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public UserData documentType(DocumentType documentType) {
        this.setDocumentType(documentType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserData)) {
            return false;
        }
        return getId() != null && getId().equals(((UserData) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserData{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", secondName='" + getSecondName() + "'" +
            ", firstLastName='" + getFirstLastName() + "'" +
            ", secondLastName='" + getSecondLastName() + "'" +
            ", document='" + getDocument() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            "}";
    }
}
