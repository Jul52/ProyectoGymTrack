package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.Payment;
import co.edu.sena.gymtrack.domain.User;
import co.edu.sena.gymtrack.domain.UserData;
import co.edu.sena.gymtrack.repository.PaymentRepository;
import co.edu.sena.gymtrack.repository.UserDataRepository;
import co.edu.sena.gymtrack.repository.UserRepository; // Revisa el nombre exacto de tu repositorio de User
import co.edu.sena.gymtrack.service.PaymentService;
import co.edu.sena.gymtrack.service.dto.PaymentDTO;
import co.edu.sena.gymtrack.service.mapper.PaymentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.Payment}.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final UserRepository userRepository;

    private final UserDataRepository userDataRepository;

    public PaymentServiceImpl(
        PaymentRepository paymentRepository,
        PaymentMapper paymentMapper,
        UserRepository userRepository,
        UserDataRepository userDataRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.userRepository = userRepository;
        this.userDataRepository = userDataRepository;
    }

    @Override
    public PaymentDTO save(PaymentDTO paymentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLogin = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                // Caso estándar de UserDetails (funciona si la autenticación es por sesión/login básico)
                userLogin = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                // Caso donde el principal es solo el nombre de usuario (ej: 'anonymousUser')
                userLogin = (String) principal;
            } else if (principal instanceof Jwt) {
                // ¡ESTO ES CRÍTICO EN JHIPSTER/OAUTH2!
                // Si usas JWT, el login se almacena en el claim 'sub' o 'preferred_username'.
                userLogin = ((Jwt) principal).getClaimAsString("preferred_username");
                if (userLogin == null) {
                    userLogin = ((Jwt) principal).getSubject(); // Intentar con 'sub'
                }
            }

            // Descartar usuarios anónimos o no auténticos antes de continuar
            if ("anonymoususer".equalsIgnoreCase(userLogin)) {
                userLogin = null;
            }
        }

        if (userLogin != null) {
            // --- LA LÓGICA DE NEGOCIO EMPIEZA AQUÍ ---

            // 1. Buscar UserData
            Optional<UserData> registeredByUserData = userDataRepository.findOneByUserLogin(userLogin);

            if (registeredByUserData.isPresent()) {
                // 2. Asignar y guardar
                Payment payment = paymentMapper.toEntity(paymentDTO);
                payment.setRegisteredBy(registeredByUserData.get());

                // Generación de transactionId opcional (sin cambios)
                if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
                    String newTransactionId = "TXN-" + System.currentTimeMillis();
                    payment.setTransactionId(newTransactionId);
                }

                payment = paymentRepository.save(payment);
                return paymentMapper.toDto(payment);
            } else {
                // ESTE ERROR ES CLARO, INDICA QUE LA BASE DE DATOS USERDATA NO TIENE AL USUARIO
                throw new RuntimeException("Error: Datos de usuario (UserData) no encontrados para: " + userLogin);
            }
        } else {
            // --- EL CÓDIGO CAÍA ANTES AQUÍ ---
            // Si el userLogin sigue siendo null, es que NO ESTÁ AUTENTICADO.
            throw new RuntimeException("No hay usuario autenticado.");
        }
    }

    @Override
    public PaymentDTO update(PaymentDTO paymentDTO) {
        LOG.debug("Request to update Payment : {}", paymentDTO);
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public Optional<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        LOG.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .map(paymentRepository::save)
            .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Payments");
        return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    }

    public Page<PaymentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return paymentRepository.findAllWithEagerRelationships(pageable).map(paymentMapper::toDto);
    }

    /**
     *  Get all the payments where Invoice is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> findAllWhereInvoiceIsNull() {
        LOG.debug("Request to get all payments where Invoice is null");
        return StreamSupport.stream(paymentRepository.findAll().spliterator(), false)
            .filter(payment -> payment.getInvoice() == null)
            .map(paymentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> findOne(Long id) {
        LOG.debug("Request to get Payment : {}", id);
        return paymentRepository.findOneWithEagerRelationships(id).map(paymentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
    }
}
