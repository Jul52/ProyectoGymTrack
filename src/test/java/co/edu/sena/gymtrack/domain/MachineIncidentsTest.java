package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.IncidentTestSamples.*;
import static co.edu.sena.gymtrack.domain.MachineIncidentsTestSamples.*;
import static co.edu.sena.gymtrack.domain.MachineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MachineIncidentsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MachineIncidents.class);
        MachineIncidents machineIncidents1 = getMachineIncidentsSample1();
        MachineIncidents machineIncidents2 = new MachineIncidents();
        assertThat(machineIncidents1).isNotEqualTo(machineIncidents2);

        machineIncidents2.setId(machineIncidents1.getId());
        assertThat(machineIncidents1).isEqualTo(machineIncidents2);

        machineIncidents2 = getMachineIncidentsSample2();
        assertThat(machineIncidents1).isNotEqualTo(machineIncidents2);
    }

    @Test
    void incidentTest() {
        MachineIncidents machineIncidents = getMachineIncidentsRandomSampleGenerator();
        Incident incidentBack = getIncidentRandomSampleGenerator();

        machineIncidents.setIncident(incidentBack);
        assertThat(machineIncidents.getIncident()).isEqualTo(incidentBack);

        machineIncidents.incident(null);
        assertThat(machineIncidents.getIncident()).isNull();
    }

    @Test
    void machineTest() {
        MachineIncidents machineIncidents = getMachineIncidentsRandomSampleGenerator();
        Machine machineBack = getMachineRandomSampleGenerator();

        machineIncidents.setMachine(machineBack);
        assertThat(machineIncidents.getMachine()).isEqualTo(machineBack);

        machineIncidents.machine(null);
        assertThat(machineIncidents.getMachine()).isNull();
    }
}
