package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.DocumentTypeTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DocumentTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocumentType.class);
        DocumentType documentType1 = getDocumentTypeSample1();
        DocumentType documentType2 = new DocumentType();
        assertThat(documentType1).isNotEqualTo(documentType2);

        documentType2.setId(documentType1.getId());
        assertThat(documentType1).isEqualTo(documentType2);

        documentType2 = getDocumentTypeSample2();
        assertThat(documentType1).isNotEqualTo(documentType2);
    }

    @Test
    void userDataTest() {
        DocumentType documentType = getDocumentTypeRandomSampleGenerator();
        UserData userDataBack = getUserDataRandomSampleGenerator();

        documentType.addUserData(userDataBack);
        assertThat(documentType.getUserData()).containsOnly(userDataBack);
        assertThat(userDataBack.getDocumentType()).isEqualTo(documentType);

        documentType.removeUserData(userDataBack);
        assertThat(documentType.getUserData()).doesNotContain(userDataBack);
        assertThat(userDataBack.getDocumentType()).isNull();

        documentType.userData(new HashSet<>(Set.of(userDataBack)));
        assertThat(documentType.getUserData()).containsOnly(userDataBack);
        assertThat(userDataBack.getDocumentType()).isEqualTo(documentType);

        documentType.setUserData(new HashSet<>());
        assertThat(documentType.getUserData()).doesNotContain(userDataBack);
        assertThat(userDataBack.getDocumentType()).isNull();
    }
}
