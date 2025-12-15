package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.MachineIncidents;
import co.edu.sena.gymtrack.repository.MachineIncidentsRepository;
import co.edu.sena.gymtrack.service.MachineIncidentsService;
import co.edu.sena.gymtrack.service.dto.MachineIncidentsDTO;
import co.edu.sena.gymtrack.service.mapper.MachineIncidentsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.MachineIncidents}.
 */
@Service
@Transactional
public class MachineIncidentsServiceImpl implements MachineIncidentsService {

    private static final Logger LOG = LoggerFactory.getLogger(MachineIncidentsServiceImpl.class);

    private final MachineIncidentsRepository machineIncidentsRepository;

    private final MachineIncidentsMapper machineIncidentsMapper;

    public MachineIncidentsServiceImpl(
        MachineIncidentsRepository machineIncidentsRepository,
        MachineIncidentsMapper machineIncidentsMapper
    ) {
        this.machineIncidentsRepository = machineIncidentsRepository;
        this.machineIncidentsMapper = machineIncidentsMapper;
    }

    @Override
    public MachineIncidentsDTO save(MachineIncidentsDTO machineIncidentsDTO) {
        LOG.debug("Request to save MachineIncidents : {}", machineIncidentsDTO);
        MachineIncidents machineIncidents = machineIncidentsMapper.toEntity(machineIncidentsDTO);
        machineIncidents = machineIncidentsRepository.save(machineIncidents);
        return machineIncidentsMapper.toDto(machineIncidents);
    }

    @Override
    public MachineIncidentsDTO update(MachineIncidentsDTO machineIncidentsDTO) {
        LOG.debug("Request to update MachineIncidents : {}", machineIncidentsDTO);
        MachineIncidents machineIncidents = machineIncidentsMapper.toEntity(machineIncidentsDTO);
        machineIncidents = machineIncidentsRepository.save(machineIncidents);
        return machineIncidentsMapper.toDto(machineIncidents);
    }

    @Override
    public Optional<MachineIncidentsDTO> partialUpdate(MachineIncidentsDTO machineIncidentsDTO) {
        LOG.debug("Request to partially update MachineIncidents : {}", machineIncidentsDTO);

        return machineIncidentsRepository
            .findById(machineIncidentsDTO.getId())
            .map(existingMachineIncidents -> {
                machineIncidentsMapper.partialUpdate(existingMachineIncidents, machineIncidentsDTO);

                return existingMachineIncidents;
            })
            .map(machineIncidentsRepository::save)
            .map(machineIncidentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineIncidentsDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MachineIncidents");
        return machineIncidentsRepository.findAll(pageable).map(machineIncidentsMapper::toDto);
    }

    public Page<MachineIncidentsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return machineIncidentsRepository.findAllWithEagerRelationships(pageable).map(machineIncidentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MachineIncidentsDTO> findOne(Long id) {
        LOG.debug("Request to get MachineIncidents : {}", id);
        return machineIncidentsRepository.findOneWithEagerRelationships(id).map(machineIncidentsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MachineIncidents : {}", id);
        machineIncidentsRepository.deleteById(id);
    }
}
