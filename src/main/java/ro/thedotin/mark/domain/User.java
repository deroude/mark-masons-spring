package ro.thedotin.mark.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "\"user\"")
public class User implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String address;

    @Column
    private String correspondenceAddress;

    @Column
    private String phoneNumber;

    @Column
    private String secondaryPhoneNumber;

    @Column
    private String profession;

    @Column
    private String workplace;

    @Column
    private String email;

    @Column
    private String userStatus;

    @Column
    private String orderPrivilege;

    @Column
    private String rank;

    @Column
    private LocalDate birthdate;

    @Column
    private String mmh;

    @Column
    private String nationalId;

    @Column
    private String nationalIdDetails;
}
