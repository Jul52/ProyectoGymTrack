package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A InvoiceService.
 */
@Entity
@Table(name = "invoice_service")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "subtotal", precision = 21, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @NotNull
    @Column(name = "sale_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal salePrice;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "payment", "invoiceServices", "paymentMethod", "userData", "service" }, allowSetters = true)
    private Invoice invoice;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "invoiceServices", "category", "reservations" }, allowSetters = true)
    private GymService service;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InvoiceService id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public InvoiceService quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public InvoiceService subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    public InvoiceService salePrice(BigDecimal salePrice) {
        this.setSalePrice(salePrice);
        return this;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public InvoiceService invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public GymService getService() {
        return this.service;
    }

    public void setService(GymService gymService) {
        this.service = gymService;
    }

    public InvoiceService service(GymService gymService) {
        this.setService(gymService);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceService)) {
            return false;
        }
        return getId() != null && getId().equals(((InvoiceService) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceService{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", subtotal=" + getSubtotal() +
            ", salePrice=" + getSalePrice() +
            "}";
    }
}
