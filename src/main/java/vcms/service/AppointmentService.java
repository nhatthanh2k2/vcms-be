package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.request.BookVaccinationRequest;
import vcms.dto.response.AppointmentResponse;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.enums.AppointmentStatus;
import vcms.enums.InjectionType;
import vcms.mapper.AppointmentMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccineMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.*;
import vcms.repository.AppointmentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private final VaccineMapper vaccineMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentMapper appointmentMapper,
                              VaccineBatchMapper vaccineBatchMapper,
                              VaccinePackageMapper vaccinePackageMapper,
                              CustomerService customerService,
                              RelativesService relativesService,
                              BatchDetailService batchDetailService,
                              VaccinePackageService vaccinePackageService, VaccineMapper vaccineMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.customerService = customerService;
        this.relativesService = relativesService;
        this.batchDetailService = batchDetailService;
        this.vaccinePackageService = vaccinePackageService;
        this.vaccineMapper = vaccineMapper;
    }

    public List<AppointmentResponse> getAppointmentListByDate(LocalDate date) {
        List<Appointment> appointmentList = appointmentRepository.findAllByAppointmentInjectionDate(date);
        if (appointmentList.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            AppointmentResponse response = appointmentMapper.toAppointmentResponse(appointment);
            Customer customer = appointment.getCustomer();
            if (customer != null) {
                response.setCustomerCode(customer.getCustomerCode());
            }
            else {
                response.setCustomerCode("Chưa có thông tin");
            }
            if (appointment.getBatchDetail() != null) {
                BatchDetailResponse batchDetailResponse =
                        vaccineBatchMapper.toBatchDetailResponse(appointment.getBatchDetail());
                batchDetailResponse.setVaccineResponse(
                        vaccineMapper.toVaccineResponse(appointment.getBatchDetail().getVaccine()));
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
            appointment.setBatchDetail(batchDetail);
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
        Appointment appointment = appointmentMapper.toAppointment(request);

        AppointmentResponse appointmentResponse = new AppointmentResponse();
        BatchDetailResponse batchDetailResponse = new BatchDetailResponse();
        VaccinePackageResponse vaccinePackageResponse = new VaccinePackageResponse();
        if (request.getApppointmentInjectionType().equals(InjectionType.SINGLE)) {
            BatchDetail batchDetail = batchDetailService.getBatchDetailById(
                    request.getAppointmentBatchDetailId());
            appointment.setBatchDetail(batchDetail);
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
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
        appointmentResponse = appointmentMapper.toAppointmentResponse(appointment);
        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }

    public AppointmentResponse createAppointmentWithCustomerCode(
            AppointmentWithCustomerCodeRequest request) {
        Appointment appointment = new Appointment();
        Customer customer = customerService.getCustomerByCustomerCode(
                request.getAppointmentCustomerCode());
        Relatives relatives = relativesService.getRelativesByCustomer(customer);

        BatchDetailResponse batchDetailResponse;
        VaccinePackageResponse vaccinePackageResponse;

        if (request.getApppointmentInjectionType().equals(
                InjectionType.SINGLE)) {
            BatchDetail batchDetail = batchDetailService.getBatchDetailById(
                    request.getAppointmentBatchDetailId());
            appointment.setBatchDetail(batchDetail);
            appointment.setAppointmentInjectionType(InjectionType.SINGLE);
            batchDetailResponse = vaccineBatchMapper.toBatchDetailResponse(batchDetail);
            vaccinePackageResponse = null;
        }
        else {
            VaccinePackage vaccinePackage =
                    vaccinePackageService.getVaccinePackageById(request.getAppointmentVaccinePackageId());
            appointment.setVaccinePackage(vaccinePackage);
            appointment.setAppointmentInjectionType(InjectionType.PACKAGE);
            vaccinePackageResponse = vaccinePackageMapper.toVaccinePackageResponse(vaccinePackage);
            batchDetailResponse = null;
        }

        appointment.setCustomer(customer);
        appointment.setAppointmentInjectionDate(request.getAppointmentInjectionDate());
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
        appointment.setAppointmentRelativesFullName(relatives.getRelativesFullName());
        appointment.setAppointmentRelativesPhone(relatives.getRelativesPhone());
        appointment.setAppointmentRelativesRelationship(relatives.getRelativesRelationship());
        appointmentRepository.save(appointment);
        AppointmentResponse appointmentResponse = appointmentMapper.toAppointmentResponse(appointment);

        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }
}
