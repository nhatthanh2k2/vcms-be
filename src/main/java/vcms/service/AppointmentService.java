package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.AppointmentCreationRequest;
import vcms.dto.response.AppointmentResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.AppointmentMapper;
import vcms.model.Appointment;
import vcms.model.BatchDetail;
import vcms.repository.AppointmentRepository;
import vcms.repository.BatchDetailRepository;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final BatchDetailRepository batchDetailRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentMapper appointmentMapper,
                              BatchDetailRepository batchDetailRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.batchDetailRepository = batchDetailRepository;
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
}
