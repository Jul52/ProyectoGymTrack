package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DocumentType.
 */
@Entity
@Table(name = "document_type")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 10)
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentType")
    @JsonIgnoreProperties(
        value = { "user", "reservations", "machines", "invoices", "courses", "payments", "documentType" },
        allowSetters = true
    )
    private Set<UserData> userData = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DocumentType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public DocumentType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public DocumentType code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<UserData> getUserData() {
        return this.userData;
    }

    public void setUserData(Set<UserData> userData) {
        if (this.userData != null) {
            this.userData.forEach(i -> i.setDocumentType(null));
        }
        if (userData != null) {
            userData.forEach(i -> i.setDocumentType(this));
        }
        this.userData = userData;
    }

    public DocumentType userData(Set<UserData> userData) {
        this.setUserData(userData);
        return this;
    }

    public DocumentType addUserData(UserData userData) {
        this.userData.add(userData);
        userData.setDocumentType(this);
        return this;
    }

    public DocumentType removeUserData(UserData userData) {
        this.userData.remove(userData);
        userData.setDocumentType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentType)) {
            return false;
        }
        return getId() != null && getId().equals(((DocumentType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
