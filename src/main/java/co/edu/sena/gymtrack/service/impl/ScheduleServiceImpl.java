package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.Schedule;
import co.edu.sena.gymtrack.repository.ScheduleRepository;
import co.edu.sena.gymtrack.service.ScheduleService;
import co.edu.sena.gymtrack.service.dto.ScheduleDTO;
import co.edu.sena.gymtrack.service.mapper.ScheduleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.Schedule}.
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        LOG.debug("Request to save Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public ScheduleDTO update(ScheduleDTO scheduleDTO) {
        LOG.debug("Request to update Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public Optional<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO) {
        LOG.debug("Request to partially update Schedule : {}", scheduleDTO);

        return scheduleRepository
            .findById(scheduleDTO.getId())
            .map(existingSchedule -> {
                scheduleMapper.partialUpdate(existingSchedule, scheduleDTO);

                return existingSchedule;
            })
            .map(scheduleRepository::save)
            .map(scheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Schedules");
        return scheduleRepository.findAll(pageable).map(scheduleMapper::toDto);
    }

    public Page<ScheduleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return scheduleRepository.findAllWithEagerRelationships(pageable).map(scheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleDTO> findOne(Long id) {
        LOG.debug("Request to get Schedule : {}", id);
        return scheduleRepository.findOneWithEagerRelationships(id).map(scheduleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Schedule : {}", id);
        scheduleRepository.deleteById(id);
    }
}
