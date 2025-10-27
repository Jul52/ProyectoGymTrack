package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.CategoryTestSamples.*;
import static co.edu.sena.gymtrack.domain.GymServiceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void gymServiceTest() {
        Category category = getCategoryRandomSampleGenerator();
        GymService gymServiceBack = getGymServiceRandomSampleGenerator();

        category.addGymService(gymServiceBack);
        assertThat(category.getGymServices()).containsOnly(gymServiceBack);
        assertThat(gymServiceBack.getCategory()).isEqualTo(category);

        category.removeGymService(gymServiceBack);
        assertThat(category.getGymServices()).doesNotContain(gymServiceBack);
        assertThat(gymServiceBack.getCategory()).isNull();

        category.gymServices(new HashSet<>(Set.of(gymServiceBack)));
        assertThat(category.getGymServices()).containsOnly(gymServiceBack);
        assertThat(gymServiceBack.getCategory()).isEqualTo(category);

        category.setGymServices(new HashSet<>());
        assertThat(category.getGymServices()).doesNotContain(gymServiceBack);
        assertThat(gymServiceBack.getCategory()).isNull();
    }
}
