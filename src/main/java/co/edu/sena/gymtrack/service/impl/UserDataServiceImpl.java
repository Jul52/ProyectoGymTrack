package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.service.UserDataService;
import co.edu.sena.gymtrack.service.dto.UserDataDTO;
import co.edu.sena.gymtrack.service.mapper.UserDataMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.UserData}.
 */
@Service
@Transactional
public class UserDataServiceImpl implements UserDataService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataServiceImpl.class);

    private final UserDataRepository userDataRepository;

    private final UserDataMapper userDataMapper;

    public UserDataServiceImpl(UserDataRepository userDataRepository, UserDataMapper userDataMapper) {
        this.userDataRepository = userDataRepository;
        this.userDataMapper = userDataMapper;
    }

    @Override
    public UserDataDTO save(UserDataDTO userDataDTO) {
        LOG.debug("Request to save UserData : {}", userDataDTO);
        UserData userData = userDataMapper.toEntity(userDataDTO);
        userData = userDataRepository.save(userData);
        return userDataMapper.toDto(userData);
    }

    @Override
    public UserDataDTO update(UserDataDTO userDataDTO) {
        LOG.debug("Request to update UserData : {}", userDataDTO);
        UserData userData = userDataMapper.toEntity(userDataDTO);
        userData = userDataRepository.save(userData);
        return userDataMapper.toDto(userData);
    }

    @Override
    public Optional<UserDataDTO> partialUpdate(UserDataDTO userDataDTO) {
        LOG.debug("Request to partially update UserData : {}", userDataDTO);

        return userDataRepository
            .findById(userDataDTO.getId())
            .map(existingUserData -> {
                userDataMapper.partialUpdate(existingUserData, userDataDTO);

                return existingUserData;
            })
            .map(userDataRepository::save)
            .map(userDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDataDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserData");
        return userDataRepository.findAll(pageable).map(userDataMapper::toDto);
    }

    public Page<UserDataDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userDataRepository.findAllWithEagerRelationships(pageable).map(userDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDataDTO> findOne(Long id) {
        LOG.debug("Request to get UserData : {}", id);
        return userDataRepository.findOneWithEagerRelationships(id).map(userDataMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserData : {}", id);
        userDataRepository.deleteById(id);
    }
}
