package ro.thedotin.mark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.thedotin.mark.domain.Article;
import ro.thedotin.mark.domain.User;
import ro.thedotin.mark.repository.ArticleRepository;
import ro.thedotin.mark.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/article")
@CrossOrigin(origins = {"http://localhost:4200", "https://mark-masons-ro.web.app"})
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_OFFICER"})
    public List<Article> getArticles(@RequestParam(name = "filter", required = false) String search, Pageable pageable) {
        return this.articleRepository.search(search, pageable).getContent();
    }

    @PostMapping
    @Secured("ROLE_OFFICER")
    public Article addArticle(@RequestBody Article article) {
        final String email = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaimAsString("email");
        final User me = this.userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        article.setAuthor(me);
        article.setPublishDate(LocalDate.now());
        this.articleRepository.saveAndFlush(article);
        return article;
    }

    @PutMapping("/{id}")
    @Secured("ROLE_OFFICER")
    public Article updateArticle(@PathVariable("id") Long id, @RequestBody Article article) {
        final String email = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaimAsString("email");
        final User me = this.userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        final Article found = this.articleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        if (!Objects.equals(found.getAuthor().getId(), me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify another user's article");
        }
        article.setAuthor(me);
        article.setPublishDate(LocalDate.now());
        this.articleRepository.saveAndFlush(article);
        return article;
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_OFFICER")
    public void deleteArticle(@PathVariable("id") Long id) {
        final String email = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaimAsString("email");
        final User me = this.userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        final Article found = this.articleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        if (!Objects.equals(found.getAuthor().getId(), me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete another user's article");
        }
        this.articleRepository.deleteById(id);
    }
}
