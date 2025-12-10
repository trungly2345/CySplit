package manytoone.Groups;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    GroupInvitation findById(int id);
    List<GroupInvitation> findAllByGroup_Id(Integer groupId);
    List<GroupInvitation> findAllByUser_Id(Integer userId);
    boolean existsByGroup_IdAndUserId(Integer groupId, Integer userId);
    boolean existsByGroup_IdAndUser_Id(Integer groupId, Integer userId);

}