package co.edu.sena.gymtrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A PaymentMethod.
 */
@Entity
@Table(name = "payment_method")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "method_name", length = 50, nullable = false)
    private String methodName;

    @NotNull
    @Size(max = 30)
    @Column(name = "method_code", length = 30, nullable = false, unique = true)
    private String methodCode;

    // Ej: STRIPE, PAYPAL, NEQUI, CASH

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "uses_gateway")
    private Boolean usesGateway = false;

    // jhipster-needle-entity-add-field

    public Long getId() {
        return this.id;
    }

    public PaymentMethod id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public PaymentMethod methodName(String methodName) {
        this.setMethodName(methodName);
        return this;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodCode() {
        return this.methodCode;
    }

    public PaymentMethod methodCode(String methodCode) {
        this.setMethodCode(methodCode);
        return this;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public Boolean getActive() {
        return this.active;
    }

    public PaymentMethod active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getUsesGateway() {
        return this.usesGateway;
    }

    public PaymentMethod usesGateway(Boolean usesGateway) {
        this.setUsesGateway(usesGateway);
        return this;
    }

    public void setUsesGateway(Boolean usesGateway) {
        this.usesGateway = usesGateway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentMethod)) {
            return false;
        }
        return getId() != null && getId().equals(((PaymentMethod) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "PaymentMethod{" +
            "id=" +
            getId() +
            ", methodName='" +
            getMethodName() +
            "'" +
            ", methodCode='" +
            getMethodCode() +
            "'" +
            ", active='" +
            getActive() +
            "'" +
            ", usesGateway='" +
            getUsesGateway() +
            "'" +
            "}"
        );
    }
}
