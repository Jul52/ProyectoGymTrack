package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A GymService.
 */
@Entity
@Table(name = "gym_service")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GymService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "service_name", length = 100, nullable = false, unique = true)
    private String serviceName;

    @Size(max = 255)
    @Column(name = "service_description", length = 255)
    private String serviceDescription;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service")
    @JsonIgnoreProperties(value = { "invoice", "service" }, allowSetters = true)
    private Set<InvoiceService> invoiceServices = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "gymServices" }, allowSetters = true)
    private Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gymService")
    @JsonIgnoreProperties(value = { "course", "gymService", "userData" }, allowSetters = true)
    private Set<Reservation> reservations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GymService id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public GymService serviceName(String serviceName) {
        this.setServiceName(serviceName);
        return this;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return this.serviceDescription;
    }

    public GymService serviceDescription(String serviceDescription) {
        this.setServiceDescription(serviceDescription);
        return this;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public GymService price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public GymService status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Set<InvoiceService> getInvoiceServices() {
        return this.invoiceServices;
    }

    public void setInvoiceServices(Set<InvoiceService> invoiceServices) {
        if (this.invoiceServices != null) {
            this.invoiceServices.forEach(i -> i.setService(null));
        }
        if (invoiceServices != null) {
            invoiceServices.forEach(i -> i.setService(this));
        }
        this.invoiceServices = invoiceServices;
    }

    public GymService invoiceServices(Set<InvoiceService> invoiceServices) {
        this.setInvoiceServices(invoiceServices);
        return this;
    }

    public GymService addInvoiceService(InvoiceService invoiceService) {
        this.invoiceServices.add(invoiceService);
        invoiceService.setService(this);
        return this;
    }

    public GymService removeInvoiceService(InvoiceService invoiceService) {
        this.invoiceServices.remove(invoiceService);
        invoiceService.setService(null);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public GymService category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(i -> i.setGymService(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setGymService(this));
        }
        this.reservations = reservations;
    }

    public GymService reservations(Set<Reservation> reservations) {
        this.setReservations(reservations);
        return this;
    }

    public GymService addReservations(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setGymService(this);
        return this;
    }

    public GymService removeReservations(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setGymService(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GymService)) {
            return false;
        }
        return getId() != null && getId().equals(((GymService) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GymService{" +
            "id=" + getId() +
            ", serviceName='" + getServiceName() + "'" +
            ", serviceDescription='" + getServiceDescription() + "'" +
            ", price=" + getPrice() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
