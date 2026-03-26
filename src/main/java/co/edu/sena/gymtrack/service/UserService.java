package co.edu.sena.gymtrack.service;

import co.edu.sena.gymtrack.config.Constants;
import co.edu.sena.gymtrack.domain.Authority;
import co.edu.sena.gymtrack.domain.DocumentType;
import co.edu.sena.gymtrack.domain.User;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.AuthorityRepository;
import co.edu.sena.gymtrack.repository.DocumentTypeRepository;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.repository.UserRepository;
import co.edu.sena.gymtrack.security.AuthoritiesConstants;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.dto.AdminUserDTO;
import co.edu.sena.gymtrack.service.dto.UserDTO;
import co.edu.sena.gymtrack.web.rest.vm.ManagedUserVM;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final UserDataRepository userDataRepository;
    private final DocumentTypeRepository documentTypeRepository;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        UserDataRepository userDataRepository,
        DocumentTypeRepository documentTypeRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.userDataRepository = userDataRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    public Optional<User> activateRegistration(String key) {
        LOG.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                user.setActivated(true);
                user.setActivationKey(null);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User registerUser(ManagedUserVM userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                if (existingUser.isActivated()) throw new RuntimeException("Login ya en uso");
                userRepository.delete(existingUser);
            });

        User newUser = new User();
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getFirstLastName()); // ✅ apellido va en lastName de User
        newUser.setEmail(userDTO.getEmail().toLowerCase());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey() != null ? userDTO.getLangKey() : Constants.DEFAULT_LANGUAGE);
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());

        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);

        // ✅ Buscar DocumentType por ID
        DocumentType documentType = documentTypeRepository
            .findById(userDTO.getDocumentType())
            .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id: " + userDTO.getDocumentType()));

        UserData userData = new UserData();
        userData.setFirstName(userDTO.getFirstName());
        userData.setSecondName(userDTO.getSecondName());
        userData.setFirstLastName(userDTO.getFirstLastName());
        userData.setSecondLastName(userDTO.getSecondLastName());
        userData.setPhone(userDTO.getPhone());
        userData.setBirthDate(userDTO.getBirthDate());
        userData.setDocumentNumber(userDTO.getDocumentNumber());
        userData.setDocumentType(documentType); // ✅ @NotNull satisfecho
        userData.setUser(newUser);
        userDataRepository.save(userData);

        LOG.debug("Created new user: {}", newUser.getLogin());
        return newUser;
    }

    public User createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        user.setLangKey(userDTO.getLangKey() == null ? Constants.DEFAULT_LANGUAGE : userDTO.getLangKey());
        user.setPassword(passwordEncoder.encode(RandomUtil.generatePassword()));
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);

        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        return user;
    }

    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional.of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setEmail(userDTO.getEmail().toLowerCase());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                LOG.debug("Deleted User: {}", user);
            });
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                if (!passwordEncoder.matches(currentClearTextPassword, user.getPassword())) {
                    throw new RuntimeException("Contraseña actual incorrecta");
                }
                user.setPassword(passwordEncoder.encode(newPassword));
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    @Transactional(readOnly = true)
    public Optional<AdminUserDTO> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).toList();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                LOG.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
            });
    }
}
