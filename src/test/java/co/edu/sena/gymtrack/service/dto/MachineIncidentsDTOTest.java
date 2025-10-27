package co.edu.sena.gymtrack.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MachineIncidentsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MachineIncidentsDTO.class);
        MachineIncidentsDTO machineIncidentsDTO1 = new MachineIncidentsDTO();
        machineIncidentsDTO1.setId(1L);
        MachineIncidentsDTO machineIncidentsDTO2 = new MachineIncidentsDTO();
        assertThat(machineIncidentsDTO1).isNotEqualTo(machineIncidentsDTO2);
        machineIncidentsDTO2.setId(machineIncidentsDTO1.getId());
        assertThat(machineIncidentsDTO1).isEqualTo(machineIncidentsDTO2);
        machineIncidentsDTO2.setId(2L);
        assertThat(machineIncidentsDTO1).isNotEqualTo(machineIncidentsDTO2);
        machineIncidentsDTO1.setId(null);
        assertThat(machineIncidentsDTO1).isNotEqualTo(machineIncidentsDTO2);
    }
}
