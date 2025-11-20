package co.edu.sena.gymtrack.domain;

import static co.edu.sena.gymtrack.domain.MachineIncidentsTestSamples.*;
import static co.edu.sena.gymtrack.domain.MachineTestSamples.*;
import static co.edu.sena.gymtrack.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.sena.gymtrack.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MachineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Machine.class);
        Machine machine1 = getMachineSample1();
        Machine machine2 = new Machine();
        assertThat(machine1).isNotEqualTo(machine2);

        machine2.setId(machine1.getId());
        assertThat(machine1).isEqualTo(machine2);

        machine2 = getMachineSample2();
        assertThat(machine1).isNotEqualTo(machine2);
    }

    @Test
    void machineIncidentsTest() {
        Machine machine = getMachineRandomSampleGenerator();
        MachineIncidents machineIncidentsBack = getMachineIncidentsRandomSampleGenerator();

        machine.addMachineIncidents(machineIncidentsBack);
        assertThat(machine.getMachineIncidents()).containsOnly(machineIncidentsBack);
        assertThat(machineIncidentsBack.getMachine()).isEqualTo(machine);

        machine.removeMachineIncidents(machineIncidentsBack);
        assertThat(machine.getMachineIncidents()).doesNotContain(machineIncidentsBack);
        assertThat(machineIncidentsBack.getMachine()).isNull();

        machine.machineIncidents(new HashSet<>(Set.of(machineIncidentsBack)));
        assertThat(machine.getMachineIncidents()).containsOnly(machineIncidentsBack);
        assertThat(machineIncidentsBack.getMachine()).isEqualTo(machine);

        machine.setMachineIncidents(new HashSet<>());
        assertThat(machine.getMachineIncidents()).doesNotContain(machineIncidentsBack);
        assertThat(machineIncidentsBack.getMachine()).isNull();
    }

    @Test
    void adminTest() {
        Machine machine = getMachineRandomSampleGenerator();
        UserData userDataBack = getUserDataRandomSampleGenerator();

        machine.setAdmin(userDataBack);
        assertThat(machine.getAdmin()).isEqualTo(userDataBack);

        machine.admin(null);
        assertThat(machine.getAdmin()).isNull();
    }
}
