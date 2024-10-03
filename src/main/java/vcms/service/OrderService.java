package vcms.service;


import org.springframework.stereotype.Service;
import vcms.dto.request.OrderCreationRequest;
import vcms.dto.request.OrderWithCustomerCodeRequest;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.OrderResponse;
import vcms.mapper.VaccineBatchMapper;
import vcms.model.BatchDetail;
import vcms.model.Customer;
import vcms.model.Order;
import vcms.model.OrderDetail;
import vcms.repository.OrderRepository;
import vcms.utils.DateService;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private final DateService dateService;

    private final VaccineBatchMapper vaccineBatchMapper;

    private final CustomerService customerService;

    private final OrderDetailService orderDetailService;

    private final BatchDetailService batchDetailService;

    public OrderService(OrderRepository orderRepository, DateService dateService,
                        VaccineBatchMapper vaccineBatchMapper, CustomerService customerService,
                        OrderDetailService orderDetailService, BatchDetailService batchDetailService) {
        this.orderRepository = orderRepository;
        this.dateService = dateService;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.customerService = customerService;
        this.orderDetailService = orderDetailService;
        this.batchDetailService = batchDetailService;
    }


    public List<OrderDetail> convertBatchDetailsToOrderDetails(
            List<BatchDetail> batchDetailList, Order order
    ) {
        return batchDetailList.stream()
                .map(batchDetail -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setBatchDetail(batchDetail);
                    return orderDetail;
                }).collect(Collectors.toList());
    }

    public OrderResponse createOrderWithCustomerCode(OrderWithCustomerCodeRequest request) {
        try {
            Order order = new Order();
            order.setOrderTotal(request.getOrderTotal());
            order.setOrderPayment(request.getOrderPayment());
            order.setOrderDate(dateService.getDateNow());
            order.setOrderInjectionDate(request.getInjectionDate());
            Customer customer = customerService.getCustomerByCustomerCode(request.getCustomerCode());
            order.setCustomer(customer);

            List<BatchDetail> batchDetailList =
                    batchDetailService.getAllBatchDetailByBatchDetailIdList(request.getOrderBatchDetailIdList());

            List<OrderDetail> orderDetails = convertBatchDetailsToOrderDetails(batchDetailList, order);

            order.setOrderDetailList(orderDetails);

            orderRepository.save(order);
            orderDetailService.insertAllOrderDetail(orderDetails);

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderTotal(request.getOrderTotal());
            orderResponse.setOrderPayment(request.getOrderPayment());
            orderResponse.setOrderDate(dateService.getDateNow());
            orderResponse.setOrderInjectionDate(request.getInjectionDate());
            orderResponse.setOrderCustomerFullName(
                    customer.getCustomerFullName());
            orderResponse.setOrderCustomerPhone(customer.getCustomerPhone());
            orderResponse.setOrderCustomerEmail(customer.getCustomerEmail());
            orderResponse.setOrderCustomerDob(customer.getCustomerDob());
            orderResponse.setOrderCustomerProvince(
                    customer.getCustomerProvince());
            orderResponse.setOrderCustomerDistrict(
                    customer.getCustomerDistrict());
            orderResponse.setOrderCustomerWard(customer.getCustomerWard());
            List<BatchDetailResponse> batchDetailResponseList =
                    batchDetailList.stream()
                            .map(vaccineBatchMapper::toBatchDetailResponse)
                            .toList();
            orderResponse.setBatchDetailResponse(batchDetailResponseList);
            return orderResponse;
        }
        catch (Exception exception) {
            return new OrderResponse();
        }
    }

    public OrderResponse createOrder(OrderCreationRequest request) {
        try {
            Order order = new Order();
            order.setOrderTotal(request.getOrderTotal());
            order.setOrderPayment(request.getOrderPayment());
            order.setOrderInjectionDate(request.getOrderInjectionDate());
            order.setOrderCustomerFullName(request.getOrderCustomerFullName());
            order.setOrderCustomerDob(request.getOrderCustomerDob());
            order.setOrderCustomerGender(request.getOrderCustomerGender());
            order.setOrderCustomerEmail(request.getOrderCustomerEmail());
            order.setOrderCustomerPhone(request.getOrderCustomerPhone());
            order.setOrderCustomerProvince(request.getOrderCustomerProvince());
            order.setOrderCustomerDistrict(request.getOrderCustomerDistrict());
            order.setOrderCustomerWard(request.getOrderCustomerWard());
            order.setOrderDate(dateService.getDateNow());
            List<BatchDetail> batchDetailList =
                    batchDetailService.getAllBatchDetailByBatchDetailIdList(request.getOrderBatchDetailIdList());

            List<OrderDetail> orderDetails =
                    convertBatchDetailsToOrderDetails(batchDetailList, order);

            order.setOrderDetailList(orderDetails);

            orderRepository.save(order);
            orderDetailService.insertAllOrderDetail(orderDetails);
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderTotal(request.getOrderTotal());
            orderResponse.setOrderPayment(request.getOrderPayment());
            orderResponse.setOrderDate(dateService.getDateNow());
            orderResponse.setOrderInjectionDate(
                    request.getOrderInjectionDate());
            orderResponse.setOrderCustomerFullName(
                    request.getOrderCustomerFullName());
            orderResponse.setOrderCustomerPhone(
                    request.getOrderCustomerPhone());
            orderResponse.setOrderCustomerEmail(
                    request.getOrderCustomerEmail());
            orderResponse.setOrderCustomerDob(request.getOrderCustomerDob());
            orderResponse.setOrderCustomerProvince(
                    request.getOrderCustomerProvince());
            orderResponse.setOrderCustomerDistrict(
                    request.getOrderCustomerDistrict());
            orderResponse.setOrderCustomerWard(request.getOrderCustomerWard());
            List<BatchDetailResponse> batchDetailResponseList = batchDetailList.stream()
                    .map(vaccineBatchMapper::toBatchDetailResponse)
                    .toList();
            orderResponse.setBatchDetailResponse(batchDetailResponseList);
            return orderResponse;
        }
        catch (Exception exception) {
            return new OrderResponse();
        }
    }
}
