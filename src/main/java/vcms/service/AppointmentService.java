package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.request.AppointmentWithCustomerCodeRequest;
import vcms.dto.response.AppointmentResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.AppointmentMapper;
import vcms.model.Appointment;
import vcms.model.BatchDetail;
import vcms.model.Customer;
import vcms.model.Relatives;
import vcms.repository.AppointmentRepository;
import vcms.repository.BatchDetailRepository;
import vcms.repository.CustomerRepository;
import vcms.repository.RelativesRepository;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final BatchDetailRepository batchDetailRepository;

    private final CustomerRepository customerRepository;

    private final RelativesRepository relativesRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentMapper appointmentMapper,
                              BatchDetailRepository batchDetailRepository,
                              CustomerRepository customerRepository,
                              RelativesRepository relativesRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.batchDetailRepository = batchDetailRepository;
        this.customerRepository = customerRepository;
        this.relativesRepository = relativesRepository;
    }

    public AppointmentResponse createAppointmentWithoutCustomerCode(
            AppointmentCreationRequest request) {
        Appointment appointment = appointmentMapper.toAppointment(request);
        BatchDetail batchDetail = batchDetailRepository.findById(
                        request.getAppointmentBatchDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        appointment.setBatchDetail(batchDetail);
        return appointmentMapper.toAppointmentResponse(
                appointmentRepository.save(appointment));
    }

    public AppointmentResponse createAppointmentWithCustomerCode(
            AppointmentWithCustomerCodeRequest request
    ) {
        Appointment appointment = new Appointment();
        Customer customer = customerRepository.findByCustomerCode(
                request.getAppointmentCustomerCode());
        Relatives relatives = relativesRepository.findByCustomer(customer);
        BatchDetail batchDetail = batchDetailRepository.findById(
                        request.getAppointmentBatchDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

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
        if (relatives != null) {
            appointment.setAppointmentRelativesFullName(
                    relatives.getRelativesFullName());
            appointment.setAppointmentRelativesPhone(
                    relatives.getRelativesPhone());
            appointment.setAppointmentRelativesRelationship(
                    relatives.getRelativesRelationship());
        }
        else {
            appointment.setAppointmentRelativesFullName("");
            appointment.setAppointmentRelativesPhone("");
            appointment.setAppointmentRelativesRelationship("");
        }

        appointment.setBatchDetail(batchDetail);
        appointment.setCustomer(customer);
        appointment.setAppointmentInjectionDate(
                request.getAppointmentInjectionDate());
        return appointmentMapper.toAppointmentResponse(
                appointmentRepository.save(appointment));
    }
}
