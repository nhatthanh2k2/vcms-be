package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.CustomerRequest;
import vcms.dto.request.LookupCustomerRequest;
import vcms.dto.response.CustomerResponse;
import vcms.enums.Gender;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.CustomerMapper;
import vcms.model.Customer;
import vcms.model.Relatives;
import vcms.repository.CustomerRepository;
import vcms.utils.DateService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final DateService dateService;

    private final RelativesService relativesService;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper,
                           DateService dateService, RelativesService relativesService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.dateService = dateService;
        this.relativesService = relativesService;
    }

    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse).toList();
    }

    public CustomerResponse getCustomer(long customerId) {
        return customerMapper.toCustomerResponse(
                customerRepository.findById(customerId).orElseThrow(
                        () -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED)));
    }

    public Customer getCustomerByCustomerCode(String code) {
        return customerRepository.findByCustomerCode(code);
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        try {
            Optional<Customer> existingCustomer = customerRepository.findByCustomerPhoneAndCustomerDob(
                    request.getCustomerPhone(), request.getCustomerDob());
            if (existingCustomer.isPresent()) {
                throw new AppException(ErrorCode.CUSTOMER_EXISTED);
            }
            Customer customer = customerMapper.toCustomer(request);
            Relatives relatives = new Relatives();
            relatives.setRelativesFullName(request.getRelativesFullName());
            relatives.setRelativesPhone(request.getRelativesPhone());
            relatives.setRelativesRelationship(request.getRelativesRelationship());

            relativesService.addRelatives(relatives);
            customerRepository.save(customer);
            relatives.setCustomer(customer);
            customer.setRelatives(relatives);
            relativesService.addRelatives(relatives);
            customerRepository.save(customer);
            LocalDate now = LocalDate.now();
            String strLocalDate = now.format(
                    DateTimeFormatter.ofPattern("ddMMyyyy"));
            String strCode =
                    "C" + strLocalDate + "-" + customer.getCustomerId();
            customer.setCustomerCode(strCode);
            return customerMapper.toCustomerResponse(customerRepository.save(customer));
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.CREATE_FAILED);
        }
    }

    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        try {
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
            customerMapper.updateCustomer(customer, request);
            return customerMapper.toCustomerResponse(
                    customerRepository.save(customer));
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.UPDATE_FAILED);
        }
    }

    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public Customer findCustomerByIdentifierAndDob(String customerIdentifier, LocalDate dob) {
        Optional<Customer> optionalCustomer = Optional.empty();
        if (customerIdentifier.startsWith("0")) {
            optionalCustomer = customerRepository.findByCustomerPhoneAndCustomerDob(
                    customerIdentifier, dob);
        }
        else if (customerIdentifier.startsWith("C")) {
            optionalCustomer = customerRepository.findByCustomerCodeAndCustomerDob(
                    customerIdentifier, dob);
        }
        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get();
        }
        throw new AppException(ErrorCode.CUSTOMER_NOT_EXISTED);
    }

    public CustomerResponse lookupCustomer(LookupCustomerRequest request) {
        Optional<Customer> optionalCustomer = Optional.empty();
        if (request.getCustomerIdentifier().startsWith("0")) {
            optionalCustomer = customerRepository.findByCustomerPhoneAndCustomerDob(
                    request.getCustomerIdentifier(),
                    request.getCustomerDob());
        }
        else if (request.getCustomerIdentifier().startsWith("C")) {
            optionalCustomer = customerRepository.findByCustomerCodeAndCustomerDob(
                    request.getCustomerIdentifier(),
                    request.getCustomerDob());
        }
        if (optionalCustomer.isPresent())
            return customerMapper.toCustomerResponse(optionalCustomer.get());
        throw new AppException(ErrorCode.CUSTOMER_NOT_EXISTED);
    }

    public void insertInitialCustomerData() {
        List<CustomerRequest> customerRequestList = new ArrayList<>();

        CustomerRequest longAnCustomer1 = new CustomerRequest(
                "Lê Văn Quý", Gender.MALE, LocalDate.of(1990, 7, 12),
                "levanquy90@gmail.com", "0351234567",
                "Tỉnh Long An", "Huyện Bến Lức", "Xã Tân Bửu",
                "Lê Thị Hoa", "0357654321", "Em"
        );

        CustomerRequest longAnCustomer2 = new CustomerRequest(
                "Nguyễn Thị Thu", Gender.FEMALE, LocalDate.of(1985, 4, 23),
                "nguyenthithu85@gmail.com", "0346543210",
                "Tỉnh Long An", "Thành phố Tân An", "Phường 2",
                "Nguyễn Văn Tài", "0341234567", "Anh"
        );

        customerRequestList.add(longAnCustomer1);
        customerRequestList.add(longAnCustomer2);

        // Tỉnh Tiền Giang
        CustomerRequest tienGiangCustomer1 = new CustomerRequest(
                "Phan Văn Khánh", Gender.MALE, LocalDate.of(1992, 8, 15),
                "phanvankhanh92@gmail.com", "0398765432",
                "Tỉnh Tiền Giang", "Thành phố Mỹ Tho", "Phường 1",
                "Phan Thị Lan", "0398765432", "Chị"
        );

        CustomerRequest tienGiangCustomer2 = new CustomerRequest(
                "Lê Thị Lan", Gender.FEMALE, LocalDate.of(1994, 11, 5),
                "lethilan94@gmail.com", "0381234567",
                "Tỉnh Tiền Giang", "Huyện Cái Bè", "Xã Mỹ Lợi A",
                "Nguyễn Văn Nam", "0386543210", "Chồng"
        );

        customerRequestList.add(tienGiangCustomer1);
        customerRequestList.add(tienGiangCustomer2);

        // Tỉnh Bến Tre
        CustomerRequest benTreCustomer1 = new CustomerRequest(
                "Trần Văn Thắng", Gender.MALE, LocalDate.of(1987, 3, 17),
                "tranvanthang87@gmail.com", "0371239876",
                "Tỉnh Bến Tre", "Thành phố Bến Tre", "Phường Phú Tân",
                "Lê Thị Thu", "0376541987", "Vợ"
        );

        CustomerRequest benTreCustomer2 = new CustomerRequest(
                "Nguyễn Thị Mai", Gender.FEMALE, LocalDate.of(1995, 12, 9),
                "nguyenthimai95@gmail.com", "0369871234",
                "Tỉnh Bến Tre", "Huyện Châu Thành", "Xã An Phước",
                "Nguyễn Văn Khoa", "0365432198", "Họ hàng"
        );

        customerRequestList.add(benTreCustomer1);
        customerRequestList.add(benTreCustomer2);

        // Tỉnh Trà Vinh
        CustomerRequest traVinhCustomer1 = new CustomerRequest(
                "Lý Văn Dũng", Gender.MALE, LocalDate.of(1991, 2, 22),
                "lyvandung91@gmail.com", "0359876543",
                "Tỉnh Trà Vinh", "Thành phố Trà Vinh", "Phường 7",
                "Lý Thị Nga", "0354321987", "Chị"
        );

        CustomerRequest traVinhCustomer2 = new CustomerRequest(
                "Phạm Thị Thùy", Gender.FEMALE, LocalDate.of(1993, 6, 18),
                "phamthithuy93@gmail.com", "0343218765",
                "Tỉnh Trà Vinh", "Huyện Càng Long", "Thị trấn Càng Long",
                "Phạm Văn Thanh", "0345674321", "Anh"
        );

        customerRequestList.add(traVinhCustomer1);
        customerRequestList.add(traVinhCustomer2);

        CustomerRequest vinhLongCustomer1 = new CustomerRequest(
                "Trần Văn Long", Gender.MALE, LocalDate.of(1985, 7, 1),
                "tranvanlong85@gmail.com", "0349871234",
                "Tỉnh Vĩnh Long", "Thành phố Vĩnh Long", "Phường 9",
                "Trần Thị Thu Hà", "0341239876", "Chị"
        );

        CustomerRequest vinhLongCustomer2 = new CustomerRequest(
                "Nguyễn Thị Đào", Gender.FEMALE, LocalDate.of(1996, 8, 3),
                "nguyenthidao96@gmail.com", "0358764321",
                "Tỉnh Vĩnh Long", "Huyện Long Hồ", "Xã An Bình",
                "Nguyễn Văn Nam", "0354321987", "Anh"
        );

        customerRequestList.add(vinhLongCustomer1);
        customerRequestList.add(vinhLongCustomer2);

        // Tỉnh Đồng Tháp
        CustomerRequest dongThapCustomer1 = new CustomerRequest(
                "Lê Minh Hùng", Gender.MALE, LocalDate.of(1989, 11, 15),
                "leminhhung89@gmail.com", "0394321234",
                "Tỉnh Đồng Tháp", "Thành phố Cao Lãnh", "Phường Hòa Thuận",
                "Lê Thị Lan", "0398765432", "Chị"
        );

        CustomerRequest dongThapCustomer2 = new CustomerRequest(
                "Phạm Thị Hương", Gender.FEMALE, LocalDate.of(1994, 5, 20),
                "phamthihuong94@gmail.com", "0375432189",
                "Tỉnh Đồng Tháp", "Thành phố Sa Đéc", "Phường 4",
                "Phạm Văn Bình", "0376543219", "Họ hàng"
        );

        customerRequestList.add(dongThapCustomer1);
        customerRequestList.add(dongThapCustomer2);

        // Tỉnh Sóc Trăng
        CustomerRequest socTrangCustomer1 = new CustomerRequest(
                "Võ Văn Phúc", Gender.MALE, LocalDate.of(1993, 12, 12),
                "vovanphuc93@gmail.com", "0383218765",
                "Tỉnh Sóc Trăng", "Thành phố Sóc Trăng", "Phường 5",
                "Võ Thị Mai", "0387654321", "Chị"
        );

        CustomerRequest socTrangCustomer2 = new CustomerRequest(
                "Lê Thị Hoa", Gender.FEMALE, LocalDate.of(1990, 2, 8),
                "lethihoa90@gmail.com", "0369876543",
                "Tỉnh Sóc Trăng", "Huyện Mỹ Xuyên", "Xã Đại Tâm",
                "Lê Văn Phú", "0365432187", "Anh"
        );

        customerRequestList.add(socTrangCustomer1);
        customerRequestList.add(socTrangCustomer2);

        // Tỉnh Bạc Liêu
        CustomerRequest bacLieuCustomer1 = new CustomerRequest(
                "Phan Văn Kiên", Gender.MALE, LocalDate.of(1988, 10, 17),
                "phanvankien88@gmail.com", "0343216547",
                "Tỉnh Bạc Liêu", "Thành phố Bạc Liêu", "Phường 1",
                "Phan Thị Vân", "0349876543", "Em"
        );

        CustomerRequest bacLieuCustomer2 = new CustomerRequest(
                "Nguyễn Thị Phương", Gender.FEMALE, LocalDate.of(1995, 4, 14),
                "nguyenthithuph95@gmail.com", "0356548765",
                "Tỉnh Bạc Liêu", "Huyện Vĩnh Lợi", "Xã Châu Thới",
                "Nguyễn Văn Hùng", "0354321789", "Anh"
        );

        customerRequestList.add(bacLieuCustomer1);
        customerRequestList.add(bacLieuCustomer2);

        // Tỉnh Cà Mau
        CustomerRequest caMauCustomer1 = new CustomerRequest(
                "Trương Văn Tuấn", Gender.MALE, LocalDate.of(1991, 6, 18),
                "truongvantuan91@gmail.com", "0398765432",
                "Tỉnh Cà Mau", "Thành phố Cà Mau", "Phường 4",
                "Trương Thị Thu", "0394321987", "Chị"
        );

        CustomerRequest caMauCustomer2 = new CustomerRequest(
                "Đặng Thị Thủy", Gender.FEMALE, LocalDate.of(1989, 9, 5),
                "dangthithuy89@gmail.com", "0373219876",
                "Tỉnh Cà Mau", "Huyện Đầm Dơi", "Xã Tân Duyệt",
                "Đặng Văn Bình", "0378765432", "Anh"
        );

        customerRequestList.add(caMauCustomer1);
        customerRequestList.add(caMauCustomer2);

        CustomerRequest child1 = new CustomerRequest(
                "Nguyễn Gia Bảo", Gender.MALE, LocalDate.of(2023, 10, 5),
                "nguyenanct@gmail.com", "0351234567",
                "Thành phố Cần Thơ", "Quận Ninh Kiều", "Phường Xuân Khánh",
                "Nguyễn Văn An", "0351234567", "Cha"
        );

        CustomerRequest child2 = new CustomerRequest(
                "Lê Minh Anh", Gender.FEMALE, LocalDate.of(2022, 2, 16),
                "lehoaag@gmail.com", "0358765432",
                "Tỉnh An Giang", "Thành phố Long Xuyên", "Phường Mỹ Bình",
                "Lê Thị Hoa", "0358765432", "Mẹ"
        );

        CustomerRequest child3 = new CustomerRequest(
                "Phạm Hoàng Anh", Gender.MALE, LocalDate.of(2021, 9, 25),
                "phamtamdt@gmail.com", "0356543210",
                "Tỉnh Đồng Tháp", "Thành phố Cao Lãnh", "Phường Hòa Thuận",
                "Phạm Văn Tâm", "0356543210", "Cha"
        );

        CustomerRequest child4 = new CustomerRequest(
                "Trần Thanh Hằng", Gender.FEMALE, LocalDate.of(2020, 11, 27),
                "thanhhang2020@gmail.com", "0351234564",
                "Tỉnh Tiền Giang", "Thành phố Mỹ Tho", "Phường 4",
                "Trần Thị Mai", "0354321876", "Mẹ"
        );

        CustomerRequest child5 = new CustomerRequest(
                "Đỗ Minh Quân", Gender.MALE, LocalDate.of(2019, 5, 27),
                "vanbinhbt@gmail.com", "0358765431",
                "Tỉnh Bến Tre", "Thành phố Bến Tre", "Phường 7",
                "Đỗ Văn Bình", "0358765431", "Cha"
        );

        CustomerRequest child6 = new CustomerRequest(
                "Lý Mỹ Linh", Gender.FEMALE, LocalDate.of(2018, 7, 1),
                "honglyhg@gmail.com", "0354321987",
                "Tỉnh Hậu Giang", "Thành phố Vị Thanh", "Phường I",
                "Lý Thị Hồng", "0354321987", "Mẹ"
        );

        CustomerRequest child7 = new CustomerRequest(
                "Nguyễn Huy Hoàng", Gender.MALE, LocalDate.of(2017, 12, 4),
                "vankhoast@gmail.com", "0356543218",
                "Tỉnh Sóc Trăng", "Thành phố Sóc Trăng", "Phường 6",
                "Nguyễn Văn Khoa", "0356543218", "Cha"
        );

        CustomerRequest child8 = new CustomerRequest(
                "Vũ Thị Thu", Gender.FEMALE, LocalDate.of(2016, 4, 5),
                "vulanbl@gmail.com", "0358765439",
                "Tỉnh Bạc Liêu", "Thành phố Bạc Liêu", "Phường 3",
                "Vũ Thị Lan", "0358765439", "Mẹ"
        );

        CustomerRequest child9 = new CustomerRequest(
                "Lê Tuấn Anh", Gender.MALE, LocalDate.of(2015, 8, 30),
                "vanphuc@gmail.com", "0354321989",
                "Tỉnh Cà Mau", "Thành phố Cà Mau", "Phường 4",
                "Lê Văn Phúc", "0354321989", "Cha"
        );

        CustomerRequest child10 = new CustomerRequest(
                "Trần Bích Ngọc", Gender.FEMALE, LocalDate.of(2014, 1, 1),
                "thihuong@gmail.com", "0356543217",
                "Tỉnh Kiên Giang", "Thành phố Rạch Giá", "Phường Vĩnh Bảo",
                "Trần Thị Hương", "0356543217", "Mẹ"
        );

        customerRequestList.add(child1);
        customerRequestList.add(child2);
        customerRequestList.add(child3);
        customerRequestList.add(child4);
        customerRequestList.add(child5);
        customerRequestList.add(child6);
        customerRequestList.add(child7);
        customerRequestList.add(child8);
        customerRequestList.add(child9);
        customerRequestList.add(child10);

        CustomerRequest teen1 = new CustomerRequest(
                "Đặng Văn Sơn", Gender.MALE, LocalDate.of(2014, 6, 12),
                "danglan@gmail.com", "0354321891",
                "Tỉnh Trà Vinh", "Thành phố Trà Vinh", "Phường 5",
                "Đặng Thị Lan", "0354321891", "Mẹ"
        );

        CustomerRequest teen2 = new CustomerRequest(
                "Hoàng Thị Lan", Gender.FEMALE, LocalDate.of(2011, 3, 20),
                "vanminh@gmail.com", "0356543215",
                "Tỉnh Vĩnh Long", "Thành phố Vĩnh Long", "Phường 9",
                "Hoàng Văn Minh", "0356543215", "Cha"
        );

        CustomerRequest teen3 = new CustomerRequest(
                "Vũ Tiến Đạt", Gender.MALE, LocalDate.of(2009, 7, 29),
                "tiendat2009@gmail.com", "0351234573",
                "Tỉnh An Giang", "Thành phố Châu Đốc", "Phường Núi Sam",
                "Vũ Thị Lan Anh", "0358765432", "Mẹ"
        );

        CustomerRequest teen4 = new CustomerRequest(
                "Lê Thị Hà", Gender.FEMALE, LocalDate.of(2008, 8, 10),
                "leha2008@gmail.com", "0351234574",
                "Tỉnh Đồng Tháp", "Thành phố Sa Đéc", "Phường Tân Quy Đông",
                "Lê Văn Cường", "0354321986", "Cha"
        );

        CustomerRequest teen5 = new CustomerRequest(
                "Trần Công Danh", Gender.MALE, LocalDate.of(2007, 5, 22),
                "congdanh2007@gmail.com", "0351234575",
                "Tỉnh Tiền Giang", "Thành phố Mỹ Tho", "Phường 10",
                "Trần Thị Thu", "0358765435", "Mẹ"
        );

        customerRequestList.add(teen1);
        customerRequestList.add(teen2);
        customerRequestList.add(teen3);
        customerRequestList.add(teen4);
        customerRequestList.add(teen5);

        try {
            for (CustomerRequest request : customerRequestList) {
                createCustomer(request);
            }
            System.out.println("Customer Data Inserted Successfully!");
        }
        catch (Exception exception) {
            System.out.println("Customer Data Inserted Failed!");
        }

    }

}
