package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.IncidentTestSamples.*;
import static co.edu.sena.gymtrack.domain.MachineIncidentsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IncidentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Incident.class);
        Incident incident1 = getIncidentSample1();
        Incident incident2 = new Incident();
        assertThat(incident1).isNotEqualTo(incident2);

        incident2.setId(incident1.getId());
        assertThat(incident1).isEqualTo(incident2);

        incident2 = getIncidentSample2();
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    void machineIncidentsTest() {
        Incident incident = getIncidentRandomSampleGenerator();
        MachineIncidents machineIncidentsBack = getMachineIncidentsRandomSampleGenerator();

        incident.addMachineIncidents(machineIncidentsBack);
        assertThat(incident.getMachineIncidents()).containsOnly(machineIncidentsBack);
        assertThat(machineIncidentsBack.getIncident()).isEqualTo(incident);

        incident.removeMachineIncidents(machineIncidentsBack);
        assertThat(incident.getMachineIncidents()).doesNotContain(machineIncidentsBack);
        assertThat(machineIncidentsBack.getIncident()).isNull();

        incident.machineIncidents(new HashSet<>(Set.of(machineIncidentsBack)));
        assertThat(incident.getMachineIncidents()).containsOnly(machineIncidentsBack);
        assertThat(machineIncidentsBack.getIncident()).isEqualTo(incident);

        incident.setMachineIncidents(new HashSet<>());
        assertThat(incident.getMachineIncidents()).doesNotContain(machineIncidentsBack);
        assertThat(machineIncidentsBack.getIncident()).isNull();
    }
}
