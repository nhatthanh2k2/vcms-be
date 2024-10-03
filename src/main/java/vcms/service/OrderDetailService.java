package vcms.service;


import org.springframework.stereotype.Service;
import vcms.model.OrderDetail;
import vcms.repository.OrderDetailRepository;

import java.util.List;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public void insertAllOrderDetail(List<OrderDetail> orderDetailList) {
        orderDetailRepository.saveAll(orderDetailList);
    }
}
