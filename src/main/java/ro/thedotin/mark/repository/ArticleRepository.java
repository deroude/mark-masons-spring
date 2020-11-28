package ro.thedotin.mark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.thedotin.mark.domain.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    @Query("select a from Article a join a.author u where (:search is null or a.title like %:search% or u.lastName like %:search% or u.firstName like %:search%)")
    Page<Article> search(@Param("search") String search, Pageable pageable);
}
