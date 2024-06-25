package vcms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "injection_schedules")
public class InjectionSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "injection_schedule_id")
    private Long injectionScheduleId;

    @Column(name = "injection_schedule_object")
    private String injectionScheduleObject;

    @Column(name = "injection_schedule_name")
    private String injectionScheduleName;

    @Column(name = "injection_schedule_content")
    private String injectionScheduleContent;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    public InjectionSchedule() {
    }

    public InjectionSchedule(Long injectionScheduleId,
                             String injectionScheduleObject,
                             String injectionScheduleName,
                             String injectionScheduleContent, Vaccine vaccine) {
        this.injectionScheduleId = injectionScheduleId;
        this.injectionScheduleObject = injectionScheduleObject;
        this.injectionScheduleName = injectionScheduleName;
        this.injectionScheduleContent = injectionScheduleContent;
        this.vaccine = vaccine;
    }

    public Long getInjectionScheduleId() {
        return injectionScheduleId;
    }

    public void setInjectionScheduleId(Long injectionScheduleId) {
        this.injectionScheduleId = injectionScheduleId;
    }

    public String getInjectionScheduleObject() {
        return injectionScheduleObject;
    }

    public void setInjectionScheduleObject(String injectionScheduleObject) {
        this.injectionScheduleObject = injectionScheduleObject;
    }

    public String getInjectionScheduleName() {
        return injectionScheduleName;
    }

    public void setInjectionScheduleName(String injectionScheduleName) {
        this.injectionScheduleName = injectionScheduleName;
    }

    public String getInjectionScheduleContent() {
        return injectionScheduleContent;
    }

    public void setInjectionScheduleContent(String injectionScheduleContent) {
        this.injectionScheduleContent = injectionScheduleContent;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }
}
