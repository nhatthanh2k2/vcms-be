package vcms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vcms.dto.request.*;
import vcms.dto.response.AppointmentResponse;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.enums.AppointmentStatus;
import vcms.enums.InjectionType;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.AppointmentMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccineMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.*;
import vcms.repository.AppointmentRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final VaccineBatchMapper vaccineBatchMapper;

    private final VaccinePackageMapper vaccinePackageMapper;

    private final CustomerService customerService;

    private final RelativesService relativesService;

    private final BatchDetailService batchDetailService;

    private final VaccinePackageService vaccinePackageService;

    private final VaccineBatchService vaccineBatchService;

    private final VaccineMapper vaccineMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentMapper appointmentMapper,
                              VaccineBatchMapper vaccineBatchMapper,
                              VaccinePackageMapper vaccinePackageMapper,
                              CustomerService customerService,
                              RelativesService relativesService,
                              BatchDetailService batchDetailService,
                              VaccinePackageService vaccinePackageService, VaccineMapper vaccineMapper,
                              VaccineBatchService vaccineBatchService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.customerService = customerService;
        this.relativesService = relativesService;
        this.batchDetailService = batchDetailService;
        this.vaccinePackageService = vaccinePackageService;
        this.vaccineMapper = vaccineMapper;
        this.vaccineBatchService = vaccineBatchService;
    }

    public Map<String, Object> getAllAppointment(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointmentPage = appointmentRepository.findAll(pageable);

        List<AppointmentResponse> appointmentResponseList =
                convertAppointmentListToAppointmentResponseList(appointmentPage.getContent());

        Map<String, Object> result = new HashMap<>();
        result.put("content", appointmentResponseList);
        result.put("totalElements", appointmentPage.getTotalElements());
        result.put("totalPages", appointmentPage.getTotalPages());
        result.put("number", appointmentPage.getNumber());
        result.put("size", appointmentPage.getSize());

        return result;
    }

    public List<AppointmentResponse> getAppointmentListByInjectionDate(LocalDate injectionDate) {
        List<Appointment> appointmentList =
                appointmentRepository.findAllByAppointmentInjectionDate(injectionDate);
        return convertAppointmentListToAppointmentResponseList(appointmentList);
    }

    public List<AppointmentResponse> getMyAppointment(LookupCustomerRequest request) {
        List<Appointment> appointmentList = appointmentRepository
                .findAllByAppointmentCustomerPhoneAndAppointmentCustomerDobOrderByAppointmentInjectionDateDesc(
                        request.getCustomerIdentifier(), request.getCustomerDob());
        return convertAppointmentListToAppointmentResponseList(appointmentList);
    }

    public Map<String, Object> getAllAppointmentListToday(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointmentPage = appointmentRepository.findAllInToday(pageable);

        List<AppointmentResponse> appointmentResponseList =
                convertAppointmentListToAppointmentResponseList(appointmentPage.getContent());

        Map<String, Object> result = new HashMap<>();
        result.put("content", appointmentResponseList);
        result.put("totalElements", appointmentPage.getTotalElements());
        result.put("totalPages", appointmentPage.getTotalPages());
        result.put("number", appointmentPage.getNumber());
        result.put("size", appointmentPage.getSize());

        return result;
    }

    public Map<String, Object> getAllAppointmentListThisWeek(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);

        Page<Appointment> appointmentPage =
                appointmentRepository.findAllByAppointmentInjectionDateBetween(today, endDate, pageable);

        List<AppointmentResponse> appointmentResponseList =
                convertAppointmentListToAppointmentResponseList(appointmentPage.getContent());

        Map<String, Object> result = new HashMap<>();
        result.put("content", appointmentResponseList);
        result.put("totalElements", appointmentPage.getTotalElements());
        result.put("totalPages", appointmentPage.getTotalPages());
        result.put("number", appointmentPage.getNumber());
        result.put("size", appointmentPage.getSize());

        return result;
    }


    public List<AppointmentResponse> getCanceledAppointmentList() {
        List<Appointment> appointmentList = appointmentRepository.findAllByAppointmentStatus(
                AppointmentStatus.CANCELLED);
        return convertAppointmentListToAppointmentResponseList(appointmentList);
    }

    public BatchDetail findBatchDetailByVaccine(Vaccine targetVaccine) {
        List<BatchDetail> batchDetailList = vaccineBatchService.getDetailListOfSampleBatch();
        return batchDetailList.stream()
                .filter(batchDetail -> batchDetail.getVaccine().equals(targetVaccine))
                .findFirst()
                .orElse(null);
    }

    private List<AppointmentResponse> convertAppointmentListToAppointmentResponseList(
            List<Appointment> appointmentList) {
        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();
        if (appointmentList.isEmpty()) {
            return Collections.emptyList();
        }

        for (Appointment appointment : appointmentList) {
            AppointmentResponse response = appointmentMapper.toAppointmentResponse(appointment);
            Customer customer = appointment.getCustomer();
            if (customer != null) {
                response.setCustomerCode(customer.getCustomerCode());
            }
            else {
                response.setCustomerCode("Chưa có thông tin");
            }

            if (appointment.getVaccine() != null) {

                BatchDetailResponse batchDetailResponse =
                        vaccineBatchMapper.toBatchDetailResponse(
                                findBatchDetailByVaccine(appointment.getVaccine())
                        );
                batchDetailResponse.setVaccineResponse(
                        vaccineMapper.toVaccineResponse(appointment.getVaccine()));
                response.setBatchDetailResponse(batchDetailResponse);
            }
            if (appointment.getVaccinePackage() != null) {
                response.setVaccinePackageResponse(
                        vaccinePackageMapper.toVaccinePackageResponse(appointment.getVaccinePackage()));
            }
            appointmentResponseList.add(response);
        }
        return appointmentResponseList;

    }

    public AppointmentResponse createAppointmentFromEmployee(BookVaccinationRequest request) {
        Appointment appointment = new Appointment();
        Customer customer = customerService.findCustomerByIdentifierAndDob(
                request.getCustomerIdentifier(), request.getCustomerDob());
        appointment.setCustomer(customer);
        appointment.setAppointmentInjectionDate(request.getInjectionDate());
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        // set Customer
        appointment.setAppointmentCustomerFullName(customer.getCustomerFullName());
        appointment.setAppointmentCustomerEmail(customer.getCustomerEmail());
        appointment.setAppointmentCustomerPhone(customer.getCustomerPhone());
        appointment.setAppointmentCustomerDob(customer.getCustomerDob());
        appointment.setAppointmentCustomerGender(customer.getCustomerGender());
        appointment.setAppointmentCustomerProvince(customer.getCustomerProvince());
        appointment.setAppointmentCustomerDistrict(customer.getCustomerDistrict());
        appointment.setAppointmentCustomerWard(customer.getCustomerWard());
        // set Relatives
        appointment.setAppointmentRelativesFullName(
                customer.getRelatives().getRelativesFullName());
        appointment.setAppointmentRelativesPhone(
                customer.getRelatives().getRelativesPhone());
        appointment.setAppointmentRelativesRelationship(
                customer.getRelatives().getRelativesRelationship());

        BatchDetailResponse batchDetailResponse = new BatchDetailResponse();
        VaccinePackageResponse vaccinePackageResponse = new VaccinePackageResponse();
        if (request.getInjectionType().equals(InjectionType.SINGLE)) {
            BatchDetail batchDetail = batchDetailService.getBatchDetailById(
                    request.getBatchDetailSelected());
            appointment.setAppointmentInjectionType(InjectionType.SINGLE);
            appointment.setVaccine(batchDetail.getVaccine());
            batchDetailResponse =
                    vaccineBatchMapper.toBatchDetailResponse(batchDetail);
            vaccinePackageResponse = null;
        }
        else {
            VaccinePackage vaccinePackage =
                    vaccinePackageService.getVaccinePackageById(request.getPackageSelected());
            appointment.setVaccinePackage(vaccinePackage);
            appointment.setAppointmentInjectionType(InjectionType.PACKAGE);
            appointment.setVaccinePackage(vaccinePackage);
            vaccinePackageResponse =
                    vaccinePackageMapper.toVaccinePackageResponse(vaccinePackage);
            batchDetailResponse = null;
        }
        appointmentRepository.save(appointment);
        AppointmentResponse appointmentResponse = appointmentMapper.toAppointmentResponse(appointment);
        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }

    public AppointmentResponse createAppointment(AppointmentCreationRequest request) {
        try {
            Appointment appointment = appointmentMapper.toAppointment(request);
            appointment.setAppointmentStatus(AppointmentStatus.PENDING);
            AppointmentResponse appointmentResponse = new AppointmentResponse();
            BatchDetailResponse batchDetailResponse = new BatchDetailResponse();
            VaccinePackageResponse vaccinePackageResponse = new VaccinePackageResponse();
            if (request.getApppointmentInjectionType().equals(InjectionType.SINGLE)) {
                BatchDetail batchDetail = batchDetailService.getBatchDetailById(
                        request.getAppointmentBatchDetailId());
                appointment.setVaccine(batchDetail.getVaccine());
                appointment.setAppointmentInjectionType(InjectionType.SINGLE);
                batchDetailResponse =
                        vaccineBatchMapper.toBatchDetailResponse(batchDetail);
                vaccinePackageResponse = null;
            }
            else {
                VaccinePackage vaccinePackage =
                        vaccinePackageService.getVaccinePackageById(request.getAppointmentVaccinePackageId());
                appointment.setVaccinePackage(vaccinePackage);
                appointment.setAppointmentInjectionType(InjectionType.PACKAGE);
                vaccinePackageResponse =
                        vaccinePackageMapper.toVaccinePackageResponse(vaccinePackage);
                batchDetailResponse = null;
            }

            appointmentRepository.save(appointment);
            appointmentResponse = appointmentMapper.toAppointmentResponse(appointment);
            appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
            appointmentResponse.setBatchDetailResponse(batchDetailResponse);
            return appointmentResponse;
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.CREATE_FAILED);
        }
    }

    public AppointmentResponse createAppointmentWithCustomerCode(
            AppointmentWithCustomerCodeRequest request) {
        Appointment appointment = new Appointment();
        Customer customer = customerService.findCustomerByIdentifierAndDob(
                request.getCustomerIdentifier(), request.getCustomerDob());
        Relatives relatives = relativesService.getRelativesByCustomer(customer);
        BatchDetailResponse batchDetailResponse;
        VaccinePackageResponse vaccinePackageResponse;

        if (request.getInjectionType().equals(InjectionType.SINGLE)) {
            BatchDetail batchDetail = batchDetailService.getBatchDetailById(
                    request.getBatchDetailId());
            appointment.setVaccine(batchDetail.getVaccine());
            appointment.setAppointmentInjectionType(InjectionType.SINGLE);
            batchDetailResponse = vaccineBatchMapper.toBatchDetailResponse(batchDetail);
            vaccinePackageResponse = null;
        }
        else {
            VaccinePackage vaccinePackage =
                    vaccinePackageService.getVaccinePackageById(request.getVaccinePackageId());
            appointment.setVaccinePackage(vaccinePackage);
            appointment.setAppointmentInjectionType(InjectionType.PACKAGE);
            vaccinePackageResponse = vaccinePackageMapper.toVaccinePackageResponse(vaccinePackage);
            batchDetailResponse = null;
        }
        appointment.setCustomer(customer);
        appointment.setAppointmentInjectionDate(request.getInjectionDate());
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointment.setAppointmentCustomerFullName(customer.getCustomerFullName());
        appointment.setAppointmentCustomerEmail(customer.getCustomerEmail());
        appointment.setAppointmentCustomerPhone(customer.getCustomerPhone());
        appointment.setAppointmentCustomerDob(customer.getCustomerDob());
        appointment.setAppointmentCustomerGender(customer.getCustomerGender());
        appointment.setAppointmentCustomerProvince(customer.getCustomerProvince());
        appointment.setAppointmentCustomerDistrict(customer.getCustomerDistrict());
        appointment.setAppointmentCustomerWard(customer.getCustomerWard());
        appointment.setAppointmentRelativesFullName(relatives.getRelativesFullName());
        appointment.setAppointmentRelativesPhone(relatives.getRelativesPhone());
        appointment.setAppointmentRelativesRelationship(relatives.getRelativesRelationship());
        appointmentRepository.save(appointment);
        AppointmentResponse appointmentResponse = appointmentMapper.toAppointmentResponse(appointment);
        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }

    public String updateAppointmentStatus(UpdateAppointmentStatusRequest request) {
        try {
            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            appointment.setAppointmentStatus(request.getAppointmentStatus());
            appointmentRepository.save(appointment);
            return "Update Appointment Status Successfully!";
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.UPDATE_FAILED);
        }
    }
}
