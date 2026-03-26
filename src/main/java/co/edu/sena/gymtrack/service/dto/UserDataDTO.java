package co.edu.sena.gymtrack.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link co.edu.sena.gymtrack.domain.UserData} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDataDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String secondName;

    @NotNull
    @Size(max = 100)
    private String firstLastName;

    @Size(max = 100)
    private String secondLastName;

    @NotNull
    @Size(max = 20)
    private String documentNumber; // ✅ CORREGIDO: era "document"

    @NotNull
    @Size(max = 20)
    private String phone; // ✅ CORREGIDO: era "phoneNumber"

    private LocalDate birthDate;

    @NotNull
    private UserDTO user;

    @NotNull
    private DocumentTypeDTO documentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    } // ✅ CORREGIDO

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    } // ✅ CORREGIDO

    public String getPhone() {
        return phone;
    } // ✅ CORREGIDO

    public void setPhone(String phone) {
        this.phone = phone;
    } // ✅ CORREGIDO

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public DocumentTypeDTO getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypeDTO documentType) {
        this.documentType = documentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDataDTO)) return false;
        UserDataDTO userDataDTO = (UserDataDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, userDataDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "UserDataDTO{" +
            "id=" +
            getId() +
            ", firstName='" +
            getFirstName() +
            "'" +
            ", secondName='" +
            getSecondName() +
            "'" +
            ", firstLastName='" +
            getFirstLastName() +
            "'" +
            ", secondLastName='" +
            getSecondLastName() +
            "'" +
            ", documentNumber='" +
            getDocumentNumber() +
            "'" + // ✅ CORREGIDO
            ", phone='" +
            getPhone() +
            "'" + // ✅ CORREGIDO
            ", birthDate='" +
            getBirthDate() +
            "'" +
            ", user=" +
            getUser() +
            ", documentType=" +
            getDocumentType() +
            "}"
        );
    }
}
