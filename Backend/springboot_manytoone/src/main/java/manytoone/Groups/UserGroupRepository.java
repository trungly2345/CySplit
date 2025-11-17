package manytoone.Groups;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
    
    List<UserGroup> findByUserId(int userId);
    
    List<UserGroup> findByGroupId(int groupId);
    
    Optional<UserGroup> findByUserIdAndGroupId(int userId, int groupId);
    
    boolean existsByUserIdAndGroupId(int userId, int groupId);
    
    @Query("SELECT ug FROM UserGroup ug WHERE ug.group.id = :groupId AND ug.role = :role")
    List<UserGroup> findByGroupIdAndRole(@Param("groupId") int groupId, @Param("role") UserGroup.Role role);
    
    @Query("SELECT ug FROM UserGroup ug WHERE ug.user.id = :userId AND ug.isActive = true")
    List<UserGroup> findActiveGroupsByUserId(@Param("userId") int userId);
    
    @Query("SELECT ug FROM UserGroup ug WHERE ug.group.id = :groupId AND ug.isActive = true")
    List<UserGroup> findActiveMembersByGroupId(@Param("groupId") int groupId);
    
    @Query("SELECT COUNT(ug) FROM UserGroup ug WHERE ug.group.id = :groupId AND ug.isActive = true")
    long countActiveMembersByGroupId(@Param("groupId") int groupId);
    
    @Query("SELECT ug FROM UserGroup ug WHERE ug.group.id = :groupId AND ug.role = 'ADMIN' AND ug.isActive = true")
    List<UserGroup> findAdminsByGroupId(@Param("groupId") int groupId);
    
    void deleteByUserIdAndGroupId(int userId, int groupId);
}