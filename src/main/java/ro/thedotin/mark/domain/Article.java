package ro.thedotin.mark.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Article {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    User author;

    @Column
    private String category;

    @Column
    private String title;

    @Column
    private String slug;

    @Column
    private String text;

    @Column
    private String tags;

    @Column
    private String language;

    @Column
    private String audience;

    @Column
    private LocalDate publishDate;

}
