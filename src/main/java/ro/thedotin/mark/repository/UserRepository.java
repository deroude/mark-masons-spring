package ro.thedotin.mark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.thedotin.mark.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
