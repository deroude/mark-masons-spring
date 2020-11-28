package ro.thedotin.mark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.thedotin.mark.domain.History;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {

    @Query("select h from History h join h.user u where u.id = :userId")
    List<History> findByUser(@Param("userId") Long id);
}
