package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.repository.GymServiceRepository;
import co.edu.sena.gymtrack.service.GymServiceService;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import co.edu.sena.gymtrack.service.mapper.GymServiceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.GymService}.
 */
@Service
@Transactional
public class GymServiceServiceImpl implements GymServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(GymServiceServiceImpl.class);

    private final GymServiceRepository gymServiceRepository;

    private final GymServiceMapper gymServiceMapper;

    public GymServiceServiceImpl(GymServiceRepository gymServiceRepository, GymServiceMapper gymServiceMapper) {
        this.gymServiceRepository = gymServiceRepository;
        this.gymServiceMapper = gymServiceMapper;
    }

    @Override
    public GymServiceDTO save(GymServiceDTO gymServiceDTO) {
        LOG.debug("Request to save GymService : {}", gymServiceDTO);
        GymService gymService = gymServiceMapper.toEntity(gymServiceDTO);
        gymService = gymServiceRepository.save(gymService);
        return gymServiceMapper.toDto(gymService);
    }

    @Override
    public GymServiceDTO update(GymServiceDTO gymServiceDTO) {
        LOG.debug("Request to update GymService : {}", gymServiceDTO);
        GymService gymService = gymServiceMapper.toEntity(gymServiceDTO);
        gymService = gymServiceRepository.save(gymService);
        return gymServiceMapper.toDto(gymService);
    }

    @Override
    public Optional<GymServiceDTO> partialUpdate(GymServiceDTO gymServiceDTO) {
        LOG.debug("Request to partially update GymService : {}", gymServiceDTO);

        return gymServiceRepository
            .findById(gymServiceDTO.getId())
            .map(existingGymService -> {
                gymServiceMapper.partialUpdate(existingGymService, gymServiceDTO);

                return existingGymService;
            })
            .map(gymServiceRepository::save)
            .map(gymServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GymServiceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all GymServices");
        return gymServiceRepository.findAll(pageable).map(gymServiceMapper::toDto);
    }

    public Page<GymServiceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return gymServiceRepository.findAllWithEagerRelationships(pageable).map(gymServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GymServiceDTO> findOne(Long id) {
        LOG.debug("Request to get GymService : {}", id);
        return gymServiceRepository.findOneWithEagerRelationships(id).map(gymServiceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete GymService : {}", id);
        gymServiceRepository.deleteById(id);
    }
}
