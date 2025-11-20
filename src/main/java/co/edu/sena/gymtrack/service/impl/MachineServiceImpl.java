package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.Machine;
import co.edu.sena.gymtrack.repository.MachineRepository;
import co.edu.sena.gymtrack.service.MachineService;
import co.edu.sena.gymtrack.service.dto.MachineDTO;
import co.edu.sena.gymtrack.service.mapper.MachineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.Machine}.
 */
@Service
@Transactional
public class MachineServiceImpl implements MachineService {

    private static final Logger LOG = LoggerFactory.getLogger(MachineServiceImpl.class);

    private final MachineRepository machineRepository;

    private final MachineMapper machineMapper;

    public MachineServiceImpl(MachineRepository machineRepository, MachineMapper machineMapper) {
        this.machineRepository = machineRepository;
        this.machineMapper = machineMapper;
    }

    @Override
    public MachineDTO save(MachineDTO machineDTO) {
        LOG.debug("Request to save Machine : {}", machineDTO);
        Machine machine = machineMapper.toEntity(machineDTO);
        machine = machineRepository.save(machine);
        return machineMapper.toDto(machine);
    }

    @Override
    public MachineDTO update(MachineDTO machineDTO) {
        LOG.debug("Request to update Machine : {}", machineDTO);
        Machine machine = machineMapper.toEntity(machineDTO);
        machine = machineRepository.save(machine);
        return machineMapper.toDto(machine);
    }

    @Override
    public Optional<MachineDTO> partialUpdate(MachineDTO machineDTO) {
        LOG.debug("Request to partially update Machine : {}", machineDTO);

        return machineRepository
            .findById(machineDTO.getId())
            .map(existingMachine -> {
                machineMapper.partialUpdate(existingMachine, machineDTO);

                return existingMachine;
            })
            .map(machineRepository::save)
            .map(machineMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Machines");
        return machineRepository.findAll(pageable).map(machineMapper::toDto);
    }

    public Page<MachineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return machineRepository.findAllWithEagerRelationships(pageable).map(machineMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MachineDTO> findOne(Long id) {
        LOG.debug("Request to get Machine : {}", id);
        return machineRepository.findOneWithEagerRelationships(id).map(machineMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Machine : {}", id);
        machineRepository.deleteById(id);
    }
}
