package vcms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "relatives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
