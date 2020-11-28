package ro.thedotin.mark.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="\"user\"", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="lodge", nullable=false)
    private Lodge lodge;

    @Column
    private String eventType;

    @Column
    private String event;

    @Column
    private String comment;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

}
