package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    @Column(name = "news_title")
    private String newsTitle;

    @Lob
    @Column(name = "news_content", columnDefinition = "TEXT")
    private String newsContent;

    @Column(name = "news_image")
    private String newsImage;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "news_createAt")
    private LocalDate newsCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "news_updateAt")
    private LocalDate newsUpdateAt;

    @Column(name = "news_status")
    private String newsStatus;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
