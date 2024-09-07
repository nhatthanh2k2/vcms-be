package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diseases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disease_id")
    private Long diseaseId;

    @Column(name = "disease_name")
    private String diseaseName;

    @Column(name = "disease_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseCreateAt;

    @Column(name = "disease_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseUpdateAt;

    @OneToMany(mappedBy = "disease", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<Vaccine> vaccineList = new ArrayList<>();

    public Disease(String diseaseName, LocalDateTime diseaseCreateAt,
                   LocalDateTime diseaseUpdateAt) {
        this.diseaseName = diseaseName;
        this.diseaseCreateAt = diseaseCreateAt;
        this.diseaseUpdateAt = diseaseUpdateAt;
    }
}
