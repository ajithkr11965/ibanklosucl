package com.sib.ibanklosucl.model.menuaccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "QUEUE_STAT")
@Immutable  // Marks the entity as read-only, since it's backed by a view
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueueStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // This is a pseudo key just to satisfy JPA's requirement

    @Column(name = "count")
    private int count;

    @Column(name = "queue")
    private String queue;

    @Column(name = "sol_id")
    private String solId;

}
