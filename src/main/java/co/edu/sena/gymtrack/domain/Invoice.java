package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Invoice.
 */
@Entity
@Table(name = "invoice")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "total", precision = 21, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "created_date")
    private Instant createdDate;

    @JsonIgnoreProperties(value = { "paymentMethod", "invoice", "registeredBy" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Payment payment;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    @JsonIgnoreProperties(value = { "invoice", "service" }, allowSetters = true)
    private Set<InvoiceService> invoiceServices = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private PaymentMethod paymentMethod;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "reservations", "machines", "invoices", "courses", "payments", "documentType" },
        allowSetters = true
    )
    private UserData userData;

    // ========================================
    // NUEVO CAMPO GYMSERVICE
    // ========================================
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "invoices", "otherFields" }, allowSetters = true) // Ajusta "otherFields" según GymService
    private GymService gymService;

    public GymService getGymService() {
        return this.gymService;
    }

    public void setGymService(GymService gymService) {
        this.gymService = gymService;
    }

    public Invoice gymService(GymService gymService) {
        this.setGymService(gymService);
        return this;
    }

    // ========================================
    // jhipster-needle-entity-add-field - JHipster will add fields here
    // ========================================

    public Long getId() {
        return this.id;
    }

    public Invoice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Invoice total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Invoice createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Invoice payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    public Set<InvoiceService> getInvoiceServices() {
        return this.invoiceServices;
    }

    public void setInvoiceServices(Set<InvoiceService> invoiceServices) {
        if (this.invoiceServices != null) {
            this.invoiceServices.forEach(i -> i.setInvoice(null));
        }
        if (invoiceServices != null) {
            invoiceServices.forEach(i -> i.setInvoice(this));
        }
        this.invoiceServices = invoiceServices;
    }

    public Invoice invoiceServices(Set<InvoiceService> invoiceServices) {
        this.setInvoiceServices(invoiceServices);
        return this;
    }

    public Invoice addInvoiceService(InvoiceService invoiceService) {
        this.invoiceServices.add(invoiceService);
        invoiceService.setInvoice(this);
        return this;
    }

    public Invoice removeInvoiceService(InvoiceService invoiceService) {
        this.invoiceServices.remove(invoiceService);
        invoiceService.setInvoice(null);
        return this;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Invoice paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public UserData getUserData() {
        return this.userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Invoice userData(UserData userData) {
        this.setUserData(userData);
        return this;
    }

    // ========================================
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
    // ========================================

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return getId() != null && getId().equals(((Invoice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Invoice{" + "id=" + getId() + ", total=" + getTotal() + ", createdDate='" + getCreatedDate() + "'" + "}";
    }
}
