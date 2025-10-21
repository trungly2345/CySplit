package manytoone.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findById(int id);
    
    User findByUserName(String userName);
    
    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.userPassword = :userPassword")
    User findByUserNameAndPassword(@Param("userName") String userName, @Param("userPassword") String userPassword);
    
    boolean existsByUserName(String userName);
}