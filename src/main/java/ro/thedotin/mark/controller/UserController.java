package ro.thedotin.mark.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ro.thedotin.mark.domain.User;
import ro.thedotin.mark.domain.dto.UserCSV;
import ro.thedotin.mark.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import static ro.thedotin.mark.domain.dto.UserCSV.dobFormat;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:4200", "https://mark-masons-ro.web.app"})
public class UserController {

    private final UserRepository userRepository;
    private final Resource clearanceTemplate;

    private final DateFormat dateFormat = new SimpleDateFormat("dd MM, yyyy");

    private final CsvMapper csv = new CsvMapper();
    private final CsvSchema csvSchema = csv.typedSchemaFor(UserCSV.class).withHeader().withUseHeader(true).withColumnReordering(true);


    @Autowired
    public UserController(UserRepository userRepository,
                          @Value("classpath:clearance.odt") Resource clearanceTemplate) {
        this.userRepository = userRepository;
        this.clearanceTemplate = clearanceTemplate;
    }

    @GetMapping
    @Secured("ROLE_OFFICER")
    List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @PostMapping
    @Secured("ROLE_OFFICER")
    User addUser(@RequestBody User u) {
        return this.userRepository.saveAndFlush(u);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_OFFICER")
    User modifyUser(@PathVariable("id") Long id, @RequestBody User u) {
        final User found = this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        found.setEmail(u.getEmail());
        found.setOrderPrivilege(u.getOrderPrivilege());
        found.setUserStatus(u.getUserStatus());
        found.setAddress(u.getAddress());
        found.setFirstName(u.getFirstName());
        found.setLastName(u.getLastName());
        found.setPhoneNumber(u.getPhoneNumber());
        found.setProfession(u.getProfession());
        found.setRank(u.getRank());
        found.setBirthdate(u.getBirthdate());
        found.setCorrespondenceAddress(u.getCorrespondenceAddress());
        found.setMmh(u.getMmh());
        found.setWorkplace(u.getWorkplace());
        found.setNationalId(u.getNationalId());
        found.setNationalIdDetails(u.getNationalIdDetails());
        found.setSecondaryPhoneNumber(u.getSecondaryPhoneNumber());
        return this.userRepository.saveAndFlush(found);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_OFFICER")
    void deleteUser(@PathVariable("id") Long id) {
        final User found = this.userRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        this.userRepository.delete(found);
    }

    @PutMapping
    @Secured("ROLE_OFFICER")
    public void uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
        csv
                .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readerFor(UserCSV.class)
                .with(csvSchema)
                .<UserCSV>readValues(file.getInputStream())
                .readAll()
                .forEach(row -> {
                    final User user = this.userRepository.findByEmail(row.getEmail()).map(u -> {
                        u.setEmail(row.getEmail());
                        u.setLastName(row.getLastName());
                        u.setFirstName(row.getFirstName());
                        u.setRank(row.getPrefix());
                        u.setAddress(MessageFormat.format("{0} {1} {2}, {3}, {4}, {5}",
                                row.getAddress(),
                                row.getAddress2(),
                                row.getAddress3(),
                                row.getCounty(),
                                row.getCity(),
                                row.getCountry()));
                        try {
                            u.setBirthdate(dobFormat.parse(row.getDob()).toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate());
                        } catch (ParseException e) {
                            // ignore
                        }
                        u.setMmh(row.getMmh());
                        u.setPhoneNumber(row.getPhone());
                        u.setSecondaryPhoneNumber(row.getSecondaryPhone());
                        return u;
                    }).orElse(row.toUser());
                    this.userRepository.save(user);
                });
    }

    @GetMapping(value = "/whoami")
    @Secured({"ROLE_USER", "ROLE_OFFICER"})
    public User whoAmI() {
        final String email = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaimAsString("email");
        return this.userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping(value = "/me/clearance", produces = MediaType.APPLICATION_PDF_VALUE)
    @Secured({"ROLE_USER", "ROLE_OFFICER"})
    public ResponseEntity<StreamingResponseBody> downloadClearancexdoc() {
        final String email = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaimAsString("email");
        final User me = this.userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        final Map<String, String> props = new HashMap<>();
        props.put("date", dateFormat.format(new Date()));
        props.put("rank", me.getRank());
        props.put("lastName", me.getLastName());
        props.put("firstName", me.getFirstName());
        props.put("lodgeName", "Nicolae Titulescu");
        props.put("lodgeNumber", "1991");
        props.put("secretaryTitle", "Bro");
        props.put("secretaryLastName", "Carata");
        props.put("secretaryFirstName", "Florin");
        props.put("mmh", me.getMmh());
        props.put("joinDate", dateFormat.format(new Date()));
        props.put("fiscalDate", dateFormat.format(new Date()));

        final StreamingResponseBody re = outputStream -> {
            try (InputStream template = clearanceTemplate.getInputStream()) {
                IXDocReport report = XDocReportRegistry.getRegistry().loadReport(template,
                        TemplateEngineKind.Velocity);

                Options options = Options.getTo(ConverterTypeTo.PDF).via(
                        ConverterTypeVia.ODFDOM);

                IContext context = report.createContext();
                context.putMap(Collections.unmodifiableMap(props));
                report.convert(context, options, outputStream);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Clearance_" + me.getLastName().toLowerCase() + ".pdf")
                .body(re);
    }
}
