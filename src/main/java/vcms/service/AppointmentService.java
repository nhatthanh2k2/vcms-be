package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.response.AppointmentResponse;
import vcms.dto.response.BatchDetailResponse;
import vcms.dto.response.VaccinePackageResponse;
import vcms.enums.AppointmentStatus;
import vcms.enums.InjectionType;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.AppointmentMapper;
import vcms.mapper.VaccineBatchMapper;
import vcms.mapper.VaccinePackageMapper;
import vcms.model.*;
import vcms.repository.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final BatchDetailRepository batchDetailRepository;

    private final CustomerRepository customerRepository;

    private final RelativesRepository relativesRepository;

    private final VaccinePackageRepository vaccinePackageRepository;

    private final VaccineBatchMapper vaccineBatchMapper;

    private final VaccinePackageMapper vaccinePackageMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentMapper appointmentMapper,
                              BatchDetailRepository batchDetailRepository,
                              CustomerRepository customerRepository,
                              RelativesRepository relativesRepository,
                              VaccinePackageRepository vaccinePackageRepository,
                              VaccineBatchMapper vaccineBatchMapper,
                              VaccinePackageMapper vaccinePackageMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.batchDetailRepository = batchDetailRepository;
        this.customerRepository = customerRepository;
        this.relativesRepository = relativesRepository;
        this.vaccinePackageRepository = vaccinePackageRepository;
        this.vaccineBatchMapper = vaccineBatchMapper;
        this.vaccinePackageMapper = vaccinePackageMapper;
    }

    public List<AppointmentResponse> getAppointmentListByDate(LocalDate date) {
        List<Appointment> appointmentList = appointmentRepository.findAllByAppointmentInjectionDate(
                date);
        if (appointmentList.isEmpty()) {
            return Collections.emptyList();
        }
        return appointmentList.stream()
                .map(appointmentMapper::toAppointmentResponse).toList();
    }

    public AppointmentResponse createAppointment(
            AppointmentCreationRequest request) {
        Appointment appointment = appointmentMapper.toAppointment(request);

        AppointmentResponse appointmentResponse = new AppointmentResponse();
        BatchDetailResponse batchDetailResponse = new BatchDetailResponse();
        VaccinePackageResponse vaccinePackageResponse =
                new VaccinePackageResponse();
        if (request.getApppointmentInjectionType().equals(
                InjectionType.SINGLE)) {
            BatchDetail batchDetail = batchDetailRepository.findById(
                            request.getAppointmentBatchDetailId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            appointment.setBatchDetail(batchDetail);
            appointment.setAppointmentInjectionType(InjectionType.SINGLE);
            batchDetailResponse =
                    vaccineBatchMapper.toBatchDetailResponse(batchDetail);
            vaccinePackageResponse = null;
        }
        else {
            VaccinePackage vaccinePackage = vaccinePackageRepository.findById(
                            request.getAppointmentVaccinePackageId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            appointment.setVaccinePackage(vaccinePackage);
            appointment.setAppointmentInjectionType(InjectionType.PACKAGE);
            vaccinePackageResponse =
                    vaccinePackageMapper.toVaccinePackageResponse(
                            vaccinePackage);
            batchDetailResponse = null;
        }

        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
        appointmentResponse =
                appointmentMapper.toAppointmentResponse(appointment);
        appointmentResponse.setApppointmentInjectionType(
                request.getApppointmentInjectionType());
        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }

    public AppointmentResponse createAppointmentWithCustomerCode(
            AppointmentWithCustomerCodeRequest request
    ) {
        Appointment appointment = new Appointment();
        Customer customer = customerRepository.findByCustomerCode(
                request.getAppointmentCustomerCode());
        Relatives relatives = relativesRepository.findByCustomer(customer);

        BatchDetailResponse batchDetailResponse;
        VaccinePackageResponse vaccinePackageResponse;

        if (request.getApppointmentInjectionType().equals(
                InjectionType.SINGLE)) {
            BatchDetail batchDetail = batchDetailRepository.findById(
                            request.getAppointmentBatchDetailId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            appointment.setBatchDetail(batchDetail);
            appointment.setAppointmentInjectionType(InjectionType.SINGLE);
            batchDetailResponse =
                    vaccineBatchMapper.toBatchDetailResponse(batchDetail);
            vaccinePackageResponse = null;
        }
        else {
            VaccinePackage vaccinePackage = vaccinePackageRepository.findById(
                            request.getAppointmentVaccinePackageId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
            appointment.setVaccinePackage(vaccinePackage);
            appointment.setAppointmentInjectionType(InjectionType.PACKAGE);
            vaccinePackageResponse =
                    vaccinePackageMapper.toVaccinePackageResponse(
                            vaccinePackage);
            batchDetailResponse = null;
        }

        appointment.setCustomer(customer);
        appointment.setAppointmentInjectionDate(
                request.getAppointmentInjectionDate());
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);

        appointment.setAppointmentCustomerFullName(
                customer.getCustomerFullName());
        appointment.setAppointmentCustomerEmail(customer.getCustomerEmail());
        appointment.setAppointmentCustomerPhone(customer.getCustomerPhone());
        appointment.setAppointmentCustomerDob(customer.getCustomerDob());
        appointment.setAppointmentCustomerGender(customer.getCustomerGender());
        appointment.setAppointmentCustomerProvince(
                customer.getCustomerProvince());
        appointment.setAppointmentCustomerDistrict(
                customer.getCustomerDistrict());
        appointment.setAppointmentCustomerWard(customer.getCustomerWard());

        appointment.setAppointmentRelativesFullName(
                    relatives.getRelativesFullName());
        appointment.setAppointmentRelativesPhone(
                    relatives.getRelativesPhone());
        appointment.setAppointmentRelativesRelationship(
                    relatives.getRelativesRelationship());
        appointmentRepository.save(appointment);
        AppointmentResponse appointmentResponse =
                appointmentMapper.toAppointmentResponse(appointment);
        appointmentResponse.setApppointmentInjectionType(
                request.getApppointmentInjectionType());
        appointmentResponse.setVaccinePackageResponse(vaccinePackageResponse);
        appointmentResponse.setBatchDetailResponse(batchDetailResponse);
        return appointmentResponse;
    }
}
