package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.InvoiceService} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceServiceDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal subtotal;

    @NotNull
    private BigDecimal salePrice;

    @NotNull
    private InvoiceDTO invoice;

    @NotNull
    private GymServiceDTO service;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    public GymServiceDTO getService() {
        return service;
    }

    public void setService(GymServiceDTO service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceServiceDTO)) {
            return false;
        }

        InvoiceServiceDTO invoiceServiceDTO = (InvoiceServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, invoiceServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceServiceDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", subtotal=" + getSubtotal() +
            ", salePrice=" + getSalePrice() +
            ", invoice=" + getInvoice() +
            ", service=" + getService() +
            "}";
    }
}
