package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Category;
import co.edu.sena.gymtrack.service.dto.CategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {}
