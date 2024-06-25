package vcms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "relatives")
public class Relatives {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relatives_id")
    private Long relativesId;

    @Column(name = "relatives_full_name")
    private String relativesFullName;

    @Column(name = "relatives_phone")
    private String relativesPhone;

    @Column(name = "relatives_email")
    private String relativesEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Relatives() {
    }

    public Relatives(Long relativesId, String relativesFullName,
                     String relativesPhone, String relativesEmail,
                     Customer customer) {
        this.relativesId = relativesId;
        this.relativesFullName = relativesFullName;
        this.relativesPhone = relativesPhone;
        this.relativesEmail = relativesEmail;
        this.customer = customer;
    }

    public Long getRelativesId() {
        return relativesId;
    }

    public void setRelativesId(Long relativesId) {
        this.relativesId = relativesId;
    }

    public String getRelativesFullName() {
        return relativesFullName;
    }

    public void setRelativesFullName(String relativesFullName) {
        this.relativesFullName = relativesFullName;
    }

    public String getRelativesPhone() {
        return relativesPhone;
    }

    public void setRelativesPhone(String relativesPhone) {
        this.relativesPhone = relativesPhone;
    }

    public String getRelativesEmail() {
        return relativesEmail;
    }

    public void setRelativesEmail(String relativesEmail) {
        this.relativesEmail = relativesEmail;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
