package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.InvoiceService;
import co.edu.sena.gymtrack.repository.GymServiceRepository;
import co.edu.sena.gymtrack.repository.InvoiceServiceRepository;
import co.edu.sena.gymtrack.security.SecurityUtils;
import co.edu.sena.gymtrack.service.InvoiceServiceService;
import co.edu.sena.gymtrack.service.dto.InvoiceServiceDTO;
import co.edu.sena.gymtrack.service.mapper.InvoiceServiceMapper;
import co.edu.sena.gymtrack.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceServiceServiceImpl implements InvoiceServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceServiceServiceImpl.class);

    private final InvoiceServiceRepository invoiceServiceRepository;
    private final InvoiceServiceMapper invoiceServiceMapper;
    private final GymServiceRepository gymServiceRepository;

    public InvoiceServiceServiceImpl(
        InvoiceServiceRepository invoiceServiceRepository,
        InvoiceServiceMapper invoiceServiceMapper,
        GymServiceRepository gymServiceRepository
    ) {
        this.invoiceServiceRepository = invoiceServiceRepository;
        this.invoiceServiceMapper = invoiceServiceMapper;
        this.gymServiceRepository = gymServiceRepository;
    }

    @Override
    public InvoiceServiceDTO save(InvoiceServiceDTO invoiceServiceDTO) {
        LOG.debug("Request to save InvoiceService : {}", invoiceServiceDTO);

        if (invoiceServiceDTO.getService() != null && invoiceServiceDTO.getService().getId() != null) {
            gymServiceRepository
                .findOneWithToOneRelationships(invoiceServiceDTO.getService().getId())
                .ifPresent(gymService -> {
                    String accessType = gymService.getCourseAccessType() != null ? gymService.getCourseAccessType().name() : "NONE";

                    if (!"NONE".equals(accessType)) {
                        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
                        if (userLogin != null) {
                            boolean hasBasicMembership = gymServiceRepository
                                .findServicesByUserLogin(userLogin)
                                .stream()
                                .anyMatch(s -> s.getCategory() != null && s.getCategory().getId() == 1L);

                            if (!hasBasicMembership) {
                                throw new BadRequestAlertException(
                                    "Debes adquirir una membresía básica antes de comprar un plan de cursos.",
                                    "invoiceService",
                                    "noBasicMembership"
                                );
                            }
                        }
                    }
                });
        }

        InvoiceService invoiceService = invoiceServiceMapper.toEntity(invoiceServiceDTO);
        invoiceService = invoiceServiceRepository.save(invoiceService);
        return invoiceServiceMapper.toDto(invoiceService);
    }

    @Override
    public InvoiceServiceDTO update(InvoiceServiceDTO invoiceServiceDTO) {
        LOG.debug("Request to update InvoiceService : {}", invoiceServiceDTO);
        InvoiceService invoiceService = invoiceServiceMapper.toEntity(invoiceServiceDTO);
        invoiceService = invoiceServiceRepository.save(invoiceService);
        return invoiceServiceMapper.toDto(invoiceService);
    }

    @Override
    public Optional<InvoiceServiceDTO> partialUpdate(InvoiceServiceDTO invoiceServiceDTO) {
        LOG.debug("Request to partially update InvoiceService : {}", invoiceServiceDTO);

        return invoiceServiceRepository
            .findById(invoiceServiceDTO.getId())
            .map(existingInvoiceService -> {
                invoiceServiceMapper.partialUpdate(existingInvoiceService, invoiceServiceDTO);
                return existingInvoiceService;
            })
            .map(invoiceServiceRepository::save)
            .map(invoiceServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceServiceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all InvoiceServices");
        return invoiceServiceRepository.findAll(pageable).map(invoiceServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceServiceDTO> findOne(Long id) {
        LOG.debug("Request to get InvoiceService : {}", id);
        return invoiceServiceRepository.findById(id).map(invoiceServiceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InvoiceService : {}", id);
        invoiceServiceRepository.deleteById(id);
    }
}
