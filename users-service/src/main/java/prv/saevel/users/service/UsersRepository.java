package prv.saevel.users.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UserModel, Long> {
    @Query("SELECT u FROM UserModel u WHERE u.status = prv.saevel.users.service.UserStatus.ACTIVE")
    List<UserModel> findActiveUsers();
}
