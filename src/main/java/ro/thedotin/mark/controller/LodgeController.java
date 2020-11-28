package ro.thedotin.mark.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ro.thedotin.mark.domain.Lodge;
import ro.thedotin.mark.domain.User;
import ro.thedotin.mark.repository.LodgeRepository;
import ro.thedotin.mark.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/lodge")
@CrossOrigin(origins = {"http://localhost:4200", "https://mark-masons-ro.web.app"})
public class LodgeController {

    private final LodgeRepository lodgeRepository;

    @Autowired
    public LodgeController(LodgeRepository lodgeRepository) {
        this.lodgeRepository = lodgeRepository;
    }

    @GetMapping
    @Secured("ROLE_OFFICER")
    List<Lodge> getLodges(){
        return this.lodgeRepository.findAll();
    }

    @PostMapping
    @Secured("ROLE_OFFICER")
    Lodge addLodge(@RequestBody Lodge u){
        return this.lodgeRepository.saveAndFlush(u);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_OFFICER")
    Lodge modifyLodge(@PathVariable("id")Long id, @RequestBody Lodge u){
        final Lodge found = this.lodgeRepository.findById(id)
                .orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
        found.setLocation(u.getLocation());
        found.setName(u.getName());
        found.setNumber(u.getNumber());
        found.setOrient(u.getOrient());
        found.setStatus(u.getStatus());
        return this.lodgeRepository.saveAndFlush(found);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_OFFICER")
    void deleteLodge(@PathVariable("id")Long id){
        final Lodge found = this.lodgeRepository.findById(id)
                .orElseThrow(()->new HttpClientErrorException(HttpStatus.NOT_FOUND));
        this.lodgeRepository.delete(found);
    }
}