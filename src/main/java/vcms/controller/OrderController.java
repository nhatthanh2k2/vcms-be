package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.OrderCreationRequest;
import vcms.dto.request.OrderWithCustomerCodeRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.OrderDetailResponse;
import vcms.dto.response.OrderResponse;
import vcms.service.OrderService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/detail/{orderId}")
    public ApiResponse<List<OrderDetailResponse>> getDetailByOrderId(@PathVariable("orderId") Long orderId) {
        return ApiResponse.<List<OrderDetailResponse>>builder().result(
                orderService.getDetailByOrderId(orderId)).build();
    }

    @PostMapping("/create-code")
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

    @PostMapping("/create")
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

    @GetMapping("/list/injection-date")
    public ApiResponse<List<OrderResponse>> getOrderListByDate(
            @RequestParam("selectedDate") String selectedDateStr
    ) {
        LocalDate selectedDate = LocalDate.parse(selectedDateStr);
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrderListByInjectionDate(
                        selectedDate))
                .build();
    }
}
