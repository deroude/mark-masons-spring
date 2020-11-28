package ro.thedotin.mark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import ro.thedotin.mark.domain.User;
import ro.thedotin.mark.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:4200", "https://mark-masons-ro.web.app"})
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    List<User> getUsers(){
        return this.userRepository.findAll();
    }

    @PostMapping
    User addUser(@RequestBody User u){
        return this.userRepository.saveAndFlush(u);
    }

    @PutMapping("/{id}")
    User modifyUser(@PathVariable("id")Long id, @RequestBody User u){
        final User found = this.userRepository.findById(id)
                .orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
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
    void deleteUser(@PathVariable("id")Long id){
        final User found = this.userRepository.findById(id)
                .orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
        this.userRepository.delete(found);
    }

    @PostMapping("/upload")
    public void uploadCsv(@RequestParam("file") MultipartFile file){

    }
}
