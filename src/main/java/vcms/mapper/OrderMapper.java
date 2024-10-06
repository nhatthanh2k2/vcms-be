package vcms.mapper;

import org.mapstruct.Mapper;
import vcms.dto.response.OrderResponse;
import vcms.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);
}
