package co.edu.sena.gymtrack.service.mapper;

import co.edu.sena.gymtrack.domain.Category;
import co.edu.sena.gymtrack.domain.GymService;
import co.edu.sena.gymtrack.service.dto.CategoryDTO;
import co.edu.sena.gymtrack.service.dto.GymServiceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GymService} and its DTO {@link GymServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface GymServiceMapper extends EntityMapper<GymServiceDTO, GymService> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryCategoryName")
    @Mapping(target = "courseAccessType", source = "courseAccessType")
    @Mapping(target = "maxReservationsPerCourse", source = "maxReservationsPerCourse")
    GymServiceDTO toDto(GymService s);

    @Named("categoryCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryName", source = "categoryName")
    CategoryDTO toDtoCategoryCategoryName(Category category);
}
