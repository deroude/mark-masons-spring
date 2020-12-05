package ro.thedotin.mark.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ro.thedotin.mark.domain.User;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCSV {

    public static final DateFormat dobFormat = new SimpleDateFormat("dd/MM/yyy");

    @JsonProperty("Brotherno")
    private String mmh;

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Surname")
    private String lastName;

    @JsonProperty("GivenNames")
    private String firstName;

    @JsonProperty("HomePhone")
    private String secondaryPhone;

    @JsonProperty("Mobile")
    private String phone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("Address2")
    private String address2;

    @JsonProperty("Address3")
    private String address3;

    @JsonProperty("City")
    private String city;

    @JsonProperty("County")
    private String county;

    @JsonProperty("PostCode")
    private String postCode;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("DOB")
    private String dob;

    public User toUser() {
        final User u = new User();
        u.setEmail(this.email);
        u.setLastName(this.lastName);
        u.setFirstName(this.firstName);
        u.setRank(this.prefix);
        u.setAddress(MessageFormat.format("{0} {1} {2}, {3}, {4}, {5}", this.address, this.address2, this.postCode, this.county, this.city, this.country));
        try {
            u.setBirthdate(dobFormat.parse(this.dob).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        } catch (ParseException e) {
            // ignore
        }
        u.setMmh(this.mmh);
        u.setPhoneNumber(this.phone);
        u.setSecondaryPhoneNumber(this.secondaryPhone);
        return u;
    }
}
