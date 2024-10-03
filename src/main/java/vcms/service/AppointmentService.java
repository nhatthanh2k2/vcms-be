package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.response.AppointmentResponse;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.enums.AppointmentStatus;
import vcms.enums.InjectionType;
import vcms.mapper.AppointmentMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.*;
import vcms.repository.AppointmentRepository;

import java.time.LocalDate;
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

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentMapper appointmentMapper,
                              VaccineBatchMapper vaccineBatchMapper,
                              VaccinePackageMapper vaccinePackageMapper,
                              CustomerService customerService,
                              RelativesService relativesService,
                              BatchDetailService batchDetailService,
                              VaccinePackageService vaccinePackageService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccinePackageMapper = vaccinePackageMapper;
        this.customerService = customerService;
        this.relativesService = relativesService;
        this.batchDetailService = batchDetailService;
        this.vaccinePackageService = vaccinePackageService;
    }

    public List<AppointmentResponse> getAppointmentListByDate(LocalDate date) {
        List<Appointment> appointmentList = appointmentRepository.findAllByAppointmentInjectionDate(date);
        if (appointmentList.isEmpty()) {
            return Collections.emptyList();
        }
        return appointmentList.stream()
                .map(appointmentMapper::toAppointmentResponse).toList();
    }

    public AppointmentResponse createAppointment(AppointmentCreationRequest request) {
        Appointment appointment = appointmentMapper.toAppointment(request);

        AppointmentResponse appointmentResponse = new AppointmentResponse();
        BatchDetailResponse batchDetailResponse = new BatchDetailResponse();
        VaccinePackageResponse vaccinePackageResponse = new VaccinePackageResponse();
        if (request.getApppointmentInjectionType().equals(
                InjectionType.SINGLE)) {
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
        appointmentResponse.setApppointmentInjectionType(request.getApppointmentInjectionType());
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
        appointmentResponse.setApppointmentInjectionType(request.getApppointmentInjectionType());
        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }
}
