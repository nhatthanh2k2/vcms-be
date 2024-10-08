package vcms.service;


import org.springframework.stereotype.Service;
import vcms.dto.request.BookVaccinationRequest;
import vcms.dto.request.CustomPackageOrderRequest;
import vcms.dto.request.OrderCreationRequest;
import vcms.dto.request.OrderWithCustomerCodeRequest;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.OrderResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.enums.InjectionType;
import vcms.mapper.OrderMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.*;
import vcms.repository.OrderRepository;
import vcms.utils.DateService;

import java.util.ArrayList;
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

    private final VaccinePackageService vaccinePackageService;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final VaccineService vaccineService;

    private final OrderMapper orderMapper;

    private final PackageDetailService packageDetailService;

    public OrderService(OrderRepository orderRepository, DateService dateService,
                        VaccineBatchMapper vaccineBatchMapper, CustomerService customerService,
                        OrderDetailService orderDetailService, BatchDetailService batchDetailService,
                        VaccinePackageService vaccinePackageService, VaccinePackageMapper vaccinePackageMapper,
                        VaccineService vaccineService, OrderMapper orderMapper,
                        PackageDetailService packageDetailService) {
        this.orderRepository = orderRepository;
        this.dateService = dateService;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.customerService = customerService;
        this.orderDetailService = orderDetailService;
        this.batchDetailService = batchDetailService;
        this.vaccinePackageService = vaccinePackageService;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.vaccineService = vaccineService;
        this.orderMapper = orderMapper;
        this.packageDetailService = packageDetailService;
    }


    public List<OrderDetail> convertBatchDetailsToOrderDetails(
            List<BatchDetail> batchDetailList, Order order) {
        return batchDetailList.stream()
                .map(batchDetail -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setBatchDetail(batchDetail);
                    return orderDetail;
                }).collect(Collectors.toList());
    }

    public List<OrderDetail> convertPackageToOrderDetails(
            List<VaccinePackage> vaccinePackageList, Order order) {
        return vaccinePackageList.stream()
                .map(vaccinePackage -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setVaccinePackage(vaccinePackage);
                    return orderDetail;
                }).collect(Collectors.toList());
    }

    public OrderResponse createCustomPackageOrder(CustomPackageOrderRequest request) {
        // tao custom package moi tu package co ban
        VaccinePackage vaccinePackage = vaccinePackageService.getVaccinePackageById(request.getVaccinePackageId());
        VaccinePackage customVaccinepackage = new VaccinePackage();
        customVaccinepackage.setVaccinePackageName(vaccinePackage.getVaccinePackageName());
        customVaccinepackage.setVaccinePackageType(vaccinePackage.getVaccinePackageType());
        vaccinePackageService.saveVaccinePackage(customVaccinepackage);

        List<Long> vaccineIdList = request.getVaccineIdList();
        List<Integer> doseCountList = request.getDoseCountList();
        int totalPrice = 0;
        List<PackageDetail> packageDetailList = new ArrayList<>();

        for (int i = 0; i < vaccineIdList.size(); i++) {
            PackageDetail packageDetail = new PackageDetail();
            Vaccine vaccine = vaccineService.getVaccineByVaccineId(vaccineIdList.get(i));
            List<BatchDetail> batchDetailList = batchDetailService.getBatchDetailByVaccine(vaccine);
            int vaccinePrice = 0;
            if (batchDetailList.size() == 1) {
                BatchDetail batchDetail = batchDetailList.getFirst();
                vaccinePrice = batchDetail.getBatchDetailVaccinePrice();
            }
            else if (batchDetailList.size() > 1) {

                int adultPrice = Math.max(
                        batchDetailList.get(0).getBatchDetailVaccinePrice(),
                        batchDetailList.get(1).getBatchDetailVaccinePrice());
                int childPrice = Math.min(
                        batchDetailList.get(0).getBatchDetailVaccinePrice(),
                        batchDetailList.get(1).getBatchDetailVaccinePrice());

                if ("ADULT".equals(customVaccinepackage.getVaccinePackageType()) ||
                        "B-PREGNANCY".equals(customVaccinepackage.getVaccinePackageType()) ||
                        "9-18Y".equals(customVaccinepackage.getVaccinePackageType())) {
                    vaccinePrice = adultPrice;
                }
                else {
                    vaccinePrice = childPrice;
                }
            }

            packageDetail.setVaccine(vaccine);
            packageDetail.setVaccinePackage(customVaccinepackage);
            packageDetail.setDoseCount(doseCountList.get(i));
            packageDetailList.add(packageDetail);
            totalPrice += vaccinePrice * doseCountList.get(i);
        }
        customVaccinepackage.setVaccinePackageCreateAt(dateService.getDateTimeNow());
        customVaccinepackage.setVaccinePackageUpdateAt(dateService.getDateTimeNow());
        customVaccinepackage.setVaccinePackagePrice(totalPrice);

        vaccinePackageService.saveVaccinePackage(customVaccinepackage);
        packageDetailService.insertAllPackageDetail(packageDetailList);
        // tao order
        Order order = new Order();
        Customer customer = customerService.
                findCustomerByIdentifierAndDob(request.getCustomerIdentifier(),
                                               request.getCustomerDob());
        order.setOrderDate(dateService.getDateNow());
        order.setOrderInjectionDate(request.getInjectionDate());
        order.setOrderPayment(request.getPayment());
        order.setOrderTotal(customVaccinepackage.getVaccinePackagePrice());
        order.setCustomer(customer);
        order.setOrderCustomerFullName(customer.getCustomerFullName());
        order.setOrderCustomerPhone(customer.getCustomerPhone());
        order.setOrderCustomerEmail(customer.getCustomerEmail());
        order.setOrderCustomerDob(customer.getCustomerDob());
        order.setOrderCustomerGender(customer.getCustomerGender());
        order.setOrderCustomerProvince(customer.getCustomerProvince());
        order.setOrderCustomerDistrict(customer.getCustomerDistrict());
        order.setOrderCustomerWard(customer.getCustomerWard());
        // tao order detail
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setVaccinePackage(customVaccinepackage);
        orderDetail.setBatchDetail(null);
        orderDetailList.add(orderDetail);
        orderRepository.save(order);
        orderDetail.setOrder(order);
        orderDetailService.insertAllOrderDetail(orderDetailList);

        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse createOrderFromEmployee(BookVaccinationRequest request) {
        Order order = new Order();
        Customer customer = customerService.findCustomerByIdentifierAndDob(request.getCustomerIdentifier(),
                                                                           request.getCustomerDob());
        BatchDetail batchDetail = new BatchDetail();
        VaccinePackage vaccinePackage = new VaccinePackage();
        int total = 0;
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        order.setCustomer(customer);
        order.setOrderCustomerFullName(customer.getCustomerFullName());
        order.setOrderCustomerPhone(customer.getCustomerPhone());
        order.setOrderCustomerEmail(customer.getCustomerEmail());
        order.setOrderCustomerDob(customer.getCustomerDob());
        order.setOrderCustomerGender(customer.getCustomerGender());
        order.setOrderCustomerProvince(customer.getCustomerProvince());
        order.setOrderCustomerDistrict(customer.getCustomerDistrict());
        order.setOrderCustomerWard(customer.getCustomerWard());

        if (request.getInjectionType().equals(InjectionType.SINGLE)) {
            batchDetail = batchDetailService.getBatchDetailById(request.getBatchDetailSelected());
            total = batchDetail.getBatchDetailVaccinePrice();
            vaccinePackage = null;
            orderDetail.setBatchDetail(batchDetail);

        }
        else {
            vaccinePackage = vaccinePackageService.getVaccinePackageById(request.getPackageSelected());
            total = vaccinePackage.getVaccinePackagePrice();
            orderDetail.setVaccinePackage(vaccinePackage);
            batchDetail = null;
        }
        order.setOrderDate(dateService.getDateNow());
        order.setOrderInjectionDate(request.getInjectionDate());
        order.setOrderPayment(request.getPayment());
        orderDetail.setOrder(order);
        orderDetailList.add(orderDetail);
        order.setOrderTotal(total);
        order.setOrderDetailList(orderDetailList);
        orderRepository.save(order);
        orderDetailService.insertAllOrderDetail(orderDetailList);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse createOrderWithCustomerCode(OrderWithCustomerCodeRequest request) {
        try {
            Order order = new Order();
            order.setOrderTotal(request.getOrderTotal());
            order.setOrderPayment(request.getOrderPayment());
            order.setOrderDate(dateService.getDateNow());
            order.setOrderInjectionDate(request.getOrderInjectionDate());
            Customer customer = customerService.getCustomerByCustomerCode(request.getCustomerCode());
            order.setCustomer(customer);

            List<BatchDetail> batchDetailList =
                    batchDetailService.getAllBatchDetailByBatchDetailIdList(request.getOrderBatchDetailIdList());
            List<VaccinePackage> vaccinePackageList = vaccinePackageService.getAllByVaccinePackageIdList(
                    request.getOrderVaccinePackageIdList());
            List<OrderDetail> orderDetailList = new ArrayList<>();
            for (BatchDetail batchDetail : batchDetailList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBatchDetail(batchDetail);
                orderDetailList.add(orderDetail);
            }
            for (VaccinePackage vaccinePackage : vaccinePackageList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setVaccinePackage(vaccinePackage);
                orderDetailList.add(orderDetail);
            }
            order.setOrderDetailList(orderDetailList);
            orderRepository.save(order);
            orderDetailService.insertAllOrderDetail(orderDetailList);

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderTotal(request.getOrderTotal());
            orderResponse.setOrderPayment(request.getOrderPayment());
            orderResponse.setOrderDate(dateService.getDateNow());
            orderResponse.setOrderInjectionDate(request.getOrderInjectionDate());
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
            List<VaccinePackageResponse> vaccinePackageResponseList = vaccinePackageList.stream()
                    .map(vaccinePackageMapper::toVaccinePackageResponse).toList();
            orderResponse.setBatchDetailResponseList(batchDetailResponseList);
            orderResponse.setVaccinePackageResponseList(vaccinePackageResponseList);
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
            List<VaccinePackage> vaccinePackageList = vaccinePackageService.getAllByVaccinePackageIdList(
                    request.getOrderVaccinePackageIdList());
            List<OrderDetail> orderDetailList = new ArrayList<>();
            for (BatchDetail batchDetail : batchDetailList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBatchDetail(batchDetail);
                orderDetailList.add(orderDetail);
            }
            for (VaccinePackage vaccinePackage : vaccinePackageList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setVaccinePackage(vaccinePackage);
                orderDetailList.add(orderDetail);
            }
            order.setOrderDetailList(orderDetailList);


            orderRepository.save(order);
            orderDetailService.insertAllOrderDetail(orderDetailList);

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
            List<VaccinePackageResponse> vaccinePackageResponseList = vaccinePackageList.stream()
                    .map(vaccinePackageMapper::toVaccinePackageResponse).toList();
            orderResponse.setBatchDetailResponseList(batchDetailResponseList);
            return orderResponse;
        }
        catch (Exception exception) {
            return new OrderResponse();
        }
    }
}
