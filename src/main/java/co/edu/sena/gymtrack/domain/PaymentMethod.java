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

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // jhipster-needle-entity-add-field - JHipster will add fields here

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

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public PaymentMethod methodCode(String methodCode) {
        this.setMethodCode(methodCode);
        return this;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PaymentMethod active(Boolean active) {
        this.setActive(active);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentMethod{" +
            "id=" + getId() +
            ", methodName='" + getMethodName() + "'" +
            "}";
    }
}
