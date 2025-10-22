package manytoone.Groups;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByUserId(int userId);
    List<UserGroup> findByGroupId(int groupId);
    Optional<UserGroup> findByUserIdAndGroupId(int userId, int groupId);
    boolean existsByUserIdAndGroupId(int userId, int groupId);
}