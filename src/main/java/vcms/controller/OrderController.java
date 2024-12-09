package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.request.OrderCreationRequest;
import vcms.dto.request.OrderWithCustomerCodeRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.OrderDetailResponse;
import vcms.dto.response.OrderResponse;
import vcms.service.OrderService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("list/all")
    public ApiResponse<Map<String, Object>> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(orderService.getAllOrder(page, size))
                .build();
    }

    @PostMapping("/my-list")
    public ApiResponse<List<OrderResponse>> getMyOrder(
            @RequestBody LookupCustomerRequest request
    ) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrder(request))
                .build();
    }

    @GetMapping("list/today")
    public ApiResponse<Map<String, Object>> getAllOrderToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(orderService.getAllOrderToday(page, size))
                .build();
    }

    @GetMapping("list/week")
    public ApiResponse<Map<String, Object>> getAllOrderInWeek(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(orderService.getAllOrderInWeek(page, size))
                .build();
    }

    @GetMapping("/detail/{orderId}")
    public ApiResponse<List<OrderDetailResponse>> getDetailByOrderId(@PathVariable("orderId") Long orderId) {
        return ApiResponse.<List<OrderDetailResponse>>builder().result(
                orderService.getDetailByOrderId(orderId)).build();
    }

    @GetMapping("/detail/my-order/{orderId}")
    public ApiResponse<List<OrderDetailResponse>> getMyOrder(@PathVariable("orderId") Long orderId) {
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
