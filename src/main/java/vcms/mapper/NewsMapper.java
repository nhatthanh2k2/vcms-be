package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.response.NewsResponse;
import vcms.model.News;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    NewsResponse toNewsResponse(News news);
}
