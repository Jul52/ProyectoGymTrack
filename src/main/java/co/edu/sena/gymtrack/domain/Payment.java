package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount_paid", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private Instant paymentDate;

    @Size(max = 100)
    @Column(name = "transaction_id", length = 100, unique = true)
    private String transactionId;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @ManyToOne(optional = false)
    @NotNull
    private PaymentMethod paymentMethod;

    @JsonIgnoreProperties(value = { "payment", "invoiceServices", "paymentMethod", "userData" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment")
    private Invoice invoice;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "reservations", "machines", "invoices", "courses", "payments", "documentType" },
        allowSetters = true
    )
    private UserData registeredBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public Payment amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Instant getPaymentDate() {
        return this.paymentDate;
    }

    public Payment paymentDate(Instant paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Payment transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return this.status;
    }

    public Payment status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Payment paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        if (this.invoice != null) {
            this.invoice.setPayment(null);
        }
        if (invoice != null) {
            invoice.setPayment(this);
        }
        this.invoice = invoice;
    }

    public Payment invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public UserData getRegisteredBy() {
        return this.registeredBy;
    }

    public void setRegisteredBy(UserData userData) {
        this.registeredBy = userData;
    }

    public Payment registeredBy(UserData userData) {
        this.setRegisteredBy(userData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", amountPaid=" + getAmountPaid() +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", transactionId='" + getTransactionId() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
