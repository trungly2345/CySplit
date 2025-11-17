package manytoone.Groups;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    GroupInvitation findById(int id);
    List<GroupInvitation> findAllByGroup_Id(Integer groupId);
    boolean existsByGroup_IdAndUserId(Integer groupId, Integer user_id);

}