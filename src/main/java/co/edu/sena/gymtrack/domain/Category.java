package co.edu.sena.gymtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    @JsonIgnoreProperties(value = { "invoiceServices", "category", "reservations" }, allowSetters = true)
    private Set<GymService> gymServices = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public Category categoryName(String categoryName) {
        this.setCategoryName(categoryName);
        return this;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<GymService> getGymServices() {
        return this.gymServices;
    }

    public void setGymServices(Set<GymService> gymServices) {
        if (this.gymServices != null) {
            this.gymServices.forEach(i -> i.setCategory(null));
        }
        if (gymServices != null) {
            gymServices.forEach(i -> i.setCategory(this));
        }
        this.gymServices = gymServices;
    }

    public Category gymServices(Set<GymService> gymServices) {
        this.setGymServices(gymServices);
        return this;
    }

    public Category addGymService(GymService gymService) {
        this.gymServices.add(gymService);
        gymService.setCategory(this);
        return this;
    }

    public Category removeGymService(GymService gymService) {
        this.gymServices.remove(gymService);
        gymService.setCategory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return getId() != null && getId().equals(((Category) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", categoryName='" + getCategoryName() + "'" +
            "}";
    }
}
