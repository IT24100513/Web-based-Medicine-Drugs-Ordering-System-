package com.sliit.pharmacy_system.repository;

import com.sliit.pharmacy_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their username.
     * @param username The username to search for.
     * @return An Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a User by their email.
     * @param email The email to search for.
     * @return An Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a User with the given username exists.
     * @param username The username to check.
     * @return True if a User with the username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a User with the given email exists.
     * @param email The email to check.
     * @return True if a User with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds all Users with a specific role.
     * @param role The UserRole to search for.
     * @return A list of User entities with the specified role.
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Finds all active Users.
     * @return A list of active User entities.
     */
    List<User> findByActiveTrue();

    /**
     * Finds all Users with a specific role and active status.
     * @param role The UserRole to search for.
     * @param active The active status to filter by.
     * @return A list of User entities matching the criteria.
     */
    List<User> findByRoleAndActive(User.UserRole role, boolean active);

    /**
     * Finds Users by role and active status, ordered by last name.
     * @param role The UserRole to search for.
     * @param active The active status to filter by.
     * @return A list of User entities ordered by last name.
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = :active ORDER BY u.lastName ASC")
    List<User> findByRoleAndActiveOrderByLastName(@Param("role") User.UserRole role, @Param("active") boolean active);

    /**
     * Searches Users by name (first name or last name contains the search term).
     * @param searchTerm The term to search for in first or last name.
     * @return A list of User entities matching the search term.
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Counts Users by role.
     * @param role The UserRole to count.
     * @return The number of Users with the specified role.
     */
    long countByRole(User.UserRole role);

    /**
     * Counts active Users by role.
     * @param role The UserRole to count.
     * @return The number of active Users with the specified role.
     */
    long countByRoleAndActive(User.UserRole role, boolean active);
}