package co.edu.sena.gymtrack.service.impl;

import co.edu.sena.gymtrack.domain.DocumentType;
import co.edu.sena.gymtrack.repository.DocumentTypeRepository;
import co.edu.sena.gymtrack.service.DocumentTypeService;
import co.edu.sena.gymtrack.service.dto.DocumentTypeDTO;
import co.edu.sena.gymtrack.service.mapper.DocumentTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.edu.sena.gymtrack.domain.DocumentType}.
 */
@Service
@Transactional
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentTypeServiceImpl.class);

    private final DocumentTypeRepository documentTypeRepository;

    private final DocumentTypeMapper documentTypeMapper;

    public DocumentTypeServiceImpl(DocumentTypeRepository documentTypeRepository, DocumentTypeMapper documentTypeMapper) {
        this.documentTypeRepository = documentTypeRepository;
        this.documentTypeMapper = documentTypeMapper;
    }

    @Override
    public DocumentTypeDTO save(DocumentTypeDTO documentTypeDTO) {
        LOG.debug("Request to save DocumentType : {}", documentTypeDTO);
        DocumentType documentType = documentTypeMapper.toEntity(documentTypeDTO);
        documentType = documentTypeRepository.save(documentType);
        return documentTypeMapper.toDto(documentType);
    }

    @Override
    public DocumentTypeDTO update(DocumentTypeDTO documentTypeDTO) {
        LOG.debug("Request to update DocumentType : {}", documentTypeDTO);
        DocumentType documentType = documentTypeMapper.toEntity(documentTypeDTO);
        documentType = documentTypeRepository.save(documentType);
        return documentTypeMapper.toDto(documentType);
    }

    @Override
    public Optional<DocumentTypeDTO> partialUpdate(DocumentTypeDTO documentTypeDTO) {
        LOG.debug("Request to partially update DocumentType : {}", documentTypeDTO);

        return documentTypeRepository
            .findById(documentTypeDTO.getId())
            .map(existingDocumentType -> {
                documentTypeMapper.partialUpdate(existingDocumentType, documentTypeDTO);

                return existingDocumentType;
            })
            .map(documentTypeRepository::save)
            .map(documentTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentTypeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DocumentTypes");
        return documentTypeRepository.findAll(pageable).map(documentTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentTypeDTO> findOne(Long id) {
        LOG.debug("Request to get DocumentType : {}", id);
        return documentTypeRepository.findById(id).map(documentTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DocumentType : {}", id);
        documentTypeRepository.deleteById(id);
    }
}
