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
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @NotNull
    @Size(max = 100)
    @Column(name = "first_last_name", nullable = false)
    private String firstLastName;

    @Column(name = "second_last_name")
    private String secondLastName;

    @NotNull
    @Size(max = 20)
    @Column(name = "document_number", nullable = false, unique = true)
    private String documentNumber;

    @NotNull
    @Size(max = 20)
    @Column(name = "phone_number", nullable = false)
    private String phone;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "registeredBy")
    @JsonIgnoreProperties(value = { "course", "gymService", "registeredBy", "schedule" }, allowSetters = true)
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

    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    // -------- METODOS FLUIDOS --------

    public UserData id(Long id) {
        this.setId(id);
        return this;
    }

    public UserData firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public UserData secondName(String secondName) {
        this.setSecondName(secondName);
        return this;
    }

    public UserData firstLastName(String firstLastName) {
        this.setFirstLastName(firstLastName);
        return this;
    }

    public UserData secondLastName(String secondLastName) {
        this.setSecondLastName(secondLastName);
        return this;
    }

    public UserData documentNumber(String documentNumber) {
        this.setDocumentNumber(documentNumber);
        return this;
    }

    public UserData phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public UserData birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public UserData user(User user) {
        this.setUser(user);
        return this;
    }

    public UserData documentType(DocumentType documentType) {
        this.setDocumentType(documentType);
        return this;
    }

    // -------- GETTERS Y SETTERS --------

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstLastName() {
        return this.firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return this.secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
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

    public DocumentType getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    // -------- RELACIONES --------

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(i -> i.setRegisteredBy(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setRegisteredBy(this));
        }
        this.reservations = reservations;
    }

    public UserData reservations(Set<Reservation> reservations) {
        this.setReservations(reservations);
        return this;
    }

    public UserData addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setRegisteredBy(this);
        return this;
    }

    public UserData removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setRegisteredBy(null);
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

    // -------- EQUALS / HASHCODE --------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData)) return false;
        return getId() != null && getId().equals(((UserData) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserData{" + "id=" + getId() + ", documentNumber='" + getDocumentNumber() + "'" + "}";
    }
}
