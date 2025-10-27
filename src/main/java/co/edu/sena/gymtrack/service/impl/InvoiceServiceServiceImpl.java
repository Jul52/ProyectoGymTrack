package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.InvoiceService;
import co.edu.sena.gymtrack.repository.InvoiceServiceRepository;
import co.edu.sena.gymtrack.service.InvoiceServiceService;
import co.edu.sena.gymtrack.service.dto.InvoiceServiceDTO;
import co.edu.sena.gymtrack.service.mapper.InvoiceServiceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.InvoiceService}.
 */
@Service
@Transactional
public class InvoiceServiceServiceImpl implements InvoiceServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceServiceServiceImpl.class);

    private final InvoiceServiceRepository invoiceServiceRepository;

    private final InvoiceServiceMapper invoiceServiceMapper;

    public InvoiceServiceServiceImpl(InvoiceServiceRepository invoiceServiceRepository, InvoiceServiceMapper invoiceServiceMapper) {
        this.invoiceServiceRepository = invoiceServiceRepository;
        this.invoiceServiceMapper = invoiceServiceMapper;
    }

    @Override
    public InvoiceServiceDTO save(InvoiceServiceDTO invoiceServiceDTO) {
        LOG.debug("Request to save InvoiceService : {}", invoiceServiceDTO);
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
