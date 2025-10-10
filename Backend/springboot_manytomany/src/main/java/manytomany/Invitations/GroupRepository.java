package manytomany.Invitations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GroupRepository extends JpaRepository<GroupInvitation, Long> {
    GroupInvitation findById(int id);

    @Transactional
    GroupInvitation deleteById(int id);


}