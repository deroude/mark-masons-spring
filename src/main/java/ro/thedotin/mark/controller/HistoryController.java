package ro.thedotin.mark.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ro.thedotin.mark.domain.History;
import ro.thedotin.mark.domain.User;
import ro.thedotin.mark.repository.HistoryRepository;
import ro.thedotin.mark.repository.UserRepository;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://mark-masons-ro.web.app"})
@RequestMapping("/user/{userId}/history")
public class HistoryController {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public HistoryController(HistoryRepository historyRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    @GetMapping()
    List<History> getHistory(@PathVariable("userId") Long userId) {
        return this.historyRepository.findByUser(userId);
    }

    @PostMapping()
    History addHistory(@PathVariable("userId") Long userId, @RequestBody History u) {
        final User found = this.userRepository.findById(userId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        u.setUser(found);
        return this.historyRepository.saveAndFlush(u);
    }

    @PutMapping("/{id}")
    History modifyHistory(@PathVariable("userId") Long userId, @PathVariable("id") Long id, @RequestBody History u) {
        final History found = this.historyRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final User userFound = this.userRepository.findById(userId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        found.setEndDate(u.getEndDate());
        found.setStartDate(u.getStartDate());
        found.setEventType(u.getEventType());
        found.setEvent(u.getEvent());
        found.setComment(u.getComment());
        found.setUser(userFound);
        return this.historyRepository.saveAndFlush(found);
    }

    @DeleteMapping("/{id}")
    void deleteHistory(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        final History found = this.historyRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        this.historyRepository.delete(found);
    }
}