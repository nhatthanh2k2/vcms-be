package vcms.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long news_id;

    private String newsTitle;

    private String newsContent;

    private String newsImage;

    private LocalDate newsCreateAt;

    private LocalDate newsUpdateAt;

    private String newsStatus;

    private String employeeFullName;

    private String employeeAvatar;
    
}
