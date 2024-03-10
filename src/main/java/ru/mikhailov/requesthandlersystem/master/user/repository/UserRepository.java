package ru.mikhailov.requesthandlersystem.master.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mikhailov.requesthandlersystem.master.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("select u.email from User u")
    List<String> findByNameOrderByEmail();

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE CONCAT('%', LOWER(:namePart), '%') ORDER BY u.id ASC")
    User findFirstUserByNamePart(@Param("namePart") String namePart);
}
