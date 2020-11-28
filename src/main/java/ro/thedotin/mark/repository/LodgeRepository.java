package ro.thedotin.mark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.thedotin.mark.domain.Lodge;

@Repository
public interface LodgeRepository extends JpaRepository<Lodge,Long> {
}
