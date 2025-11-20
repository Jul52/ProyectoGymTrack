package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.DocumentType;
import co.edu.sena.gymtrack.service.dto.DocumentTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DocumentType} and its DTO {@link DocumentTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DocumentTypeMapper extends EntityMapper<DocumentTypeDTO, DocumentType> {}
