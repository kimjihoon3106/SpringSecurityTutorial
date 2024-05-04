package springSecurityTutorial.springSecurityTutorial.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springSecurityTutorial.springSecurityTutorial.Model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
}
