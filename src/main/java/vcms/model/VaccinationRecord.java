package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "vaccination_records")
public class VaccinationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_record_id")
    private Long vaccinationRecordId;

    // Ngày tiêm
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "vaccination_record_date")
    private LocalDate vaccinationRecordDate;

    // Liều lượng tiêm
    @Column(name = "vaccination_record_dosage")
    private String vaccinationRecordDosage;

    // Mũi tiêm
    @Column(name = "vaccination_record_shot")
    private String vaccinationRecordShot;

    // Người tiêm
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Vắc-xin tiêm
    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    // nhân viên thực hiện
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public VaccinationRecord() {
    }

    public VaccinationRecord(Long vaccinationRecordId,
                             LocalDate vaccinationRecordDate,
                             String vaccinationRecordDosage,
                             String vaccinationRecordShot, Customer customer,
                             Vaccine vaccine, Employee employee) {
        this.vaccinationRecordId = vaccinationRecordId;
        this.vaccinationRecordDate = vaccinationRecordDate;
        this.vaccinationRecordDosage = vaccinationRecordDosage;
        this.vaccinationRecordShot = vaccinationRecordShot;
        this.customer = customer;
        this.vaccine = vaccine;
        this.employee = employee;
    }

    public Long getVaccinationRecordId() {
        return vaccinationRecordId;
    }

    public void setVaccinationRecordId(Long vaccinationRecordId) {
        this.vaccinationRecordId = vaccinationRecordId;
    }

    public LocalDate getVaccinationRecordDate() {
        return vaccinationRecordDate;
    }

    public void setVaccinationRecordDate(LocalDate vaccinationRecordDate) {
        this.vaccinationRecordDate = vaccinationRecordDate;
    }

    public String getVaccinationRecordDosage() {
        return vaccinationRecordDosage;
    }

    public void setVaccinationRecordDosage(String vaccinationRecordDosage) {
        this.vaccinationRecordDosage = vaccinationRecordDosage;
    }

    public String getVaccinationRecordShot() {
        return vaccinationRecordShot;
    }

    public void setVaccinationRecordShot(String vaccinationRecordShot) {
        this.vaccinationRecordShot = vaccinationRecordShot;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
