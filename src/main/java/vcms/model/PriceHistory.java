package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "price_histories")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ph_id")
    private Long priceHistoryId;

    @Column(name = "ph_old_price")
    private Integer priceHistoryOldPrice;

    @Column(name = "ph_new_price")
    private Integer priceHistoryNewPrice;

    @Column(name = "ph_update_time")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime priceHistoryUpdateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;
}
