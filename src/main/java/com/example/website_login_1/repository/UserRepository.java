package com.example.website_login_1.repository;

import com.example.website_login_1.entity.TenantUser;
import com.example.website_login_1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.tenantUserList tu " +
            "WHERE tu.tenant.id = :tenantId " +
            "AND tu.rejected = :rejected AND tu.approved= :approved")
    List<User> findByTenantIdAndApprovedAndRejected(@Param("tenantId") Long tenantId,
                                                    @Param("approved") boolean approved,
                                                    @Param("rejected") boolean rejected);

    // TODO: verify that only the tenant id in parameter is returned.
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.tenantUserList tu " +
            "WHERE tu.tenant.id = :tenantId AND u.id = :userId AND tu.user.id = :userId")
    User findByTenantIdLeftJoinFetchTenantUser(@Param("userId") UUID userId,
                                               @Param("tenantId") Long tenantId);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.tenantUserList tu " +
            "WHERE tu.tenant.id = :tenantId")
    List<User> findByTenantIdLeftJoinFetchTenantUser(@Param("tenantId") Long tenantId);

    default List<User> findByIdInLeftJoinFetchTenantAndUserTenantRoles(Set<UUID> userIds) {
        List<User> users = findByIdInLeftJoinFetchTenantUser(userIds);
        users = findByUsersInLeftJoinFetchUserTenantRole(users);
        List<TenantUser> tenantUserList = users.stream()
                .map(user -> user.getTenantUserList())
                .flatMap(List::stream)
                .toList();
        tenantUserList = findByIdInLeftJoinFetchUserTenantRole(tenantUserList);
        return users;
    }

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.tenantUserList tu " +
            "WHERE u.id IN (:userIds)")
    List<User> findByIdInLeftJoinFetchTenantUser(@Param("userIds") Set<UUID> userIds);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userTenantRoles utr " +
            "WHERE u IN (:users)")
    List<User> findByUsersInLeftJoinFetchUserTenantRole(@Param("users") List<User> users);

    @Query("SELECT tu FROM TenantUser tu " +
            "LEFT JOIN FETCH tu.tenant t " +
            "WHERE tu IN (:tenantUsers)")
    List<TenantUser> findByIdInLeftJoinFetchUserTenantRole(@Param("tenantUsers") List<TenantUser> tenantUsers);

    /*@Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.tenantUserList tu " +
            "LEFT JOIN FETCH tu.tenant t " +
            "LEFT JOIN FETCH u.userTenantRoles utr " +
            "WHERE u.id IN (:userIds)")
    List<User> findByIdInLeftJoinFetchTenantAndUserTenantRoles(@Param("userIds") Set<UUID> userIds);*/
}
