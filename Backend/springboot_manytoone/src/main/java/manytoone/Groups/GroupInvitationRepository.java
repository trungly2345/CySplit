package manytoone.Groups;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    GroupInvitation findById(int id);
    List<GroupInvitation> findAllByGroup_Id(Integer groupId);
    boolean existsByGroup_IdAndUserName(Integer groupId, String userName);

}