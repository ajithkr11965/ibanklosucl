package com.sib.ibanklosucl.model.menuaccess;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NEWS_ALERT")
@Getter
@Setter
public class NewsAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SUBTITLE")
    private String subtitle;

    @Column(name = "HREF_URL")
    private String hrefUrl;

    @Column(name = "ICON_CLASS")
    private String iconClass;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;
}
