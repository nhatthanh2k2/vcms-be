package vcms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vcms.dto.request.OrderCreationRequest;
import vcms.dto.request.OrderWithCustomerCodeRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.OrderResponse;
import vcms.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("create-code")
    public ApiResponse<?> createOrderWithCustomerCode(
            @RequestBody OrderWithCustomerCodeRequest request) {
        OrderResponse orderResponse = orderService.createOrderWithCustomerCode(
                request);
        if (orderResponse.getOrderTotal() == 0 || orderResponse.getOrderCustomerFullName() == null) {
            ApiResponse.<String>builder()
                    .result("Create Order Failed")
                    .code(1008)
                    .build();
        }
        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .build();
    }

    @PostMapping("create")
    public ApiResponse<?> createOrder(
            @RequestBody OrderCreationRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        if (orderResponse.getOrderTotal() == 0 || orderResponse.getOrderCustomerFullName() == null) {
            ApiResponse.<String>builder()
                    .result("Create Order Failed")
                    .code(1008)
                    .build();
        }
        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .build();
    }
}
