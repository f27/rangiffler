package guru.qa.rangiffler.repository;

import guru.qa.rangiffler.entity.user.UserEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserdataRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity getByUsername(@Nonnull String username);

    void deleteByUsername(@Nonnull String username);

    Slice<UserEntity> findByUsernameNot(@Nonnull String username,
                                        @Nonnull Pageable pageable);

    @Query("select u from UserEntity u where u.username <> :username" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    Slice<UserEntity> findByUsernameNotAndSearchQuery(@Param("username") String username,
                                                      @Nonnull Pageable pageable,
                                                      @Param("searchQuery") String searchQuery);

    List<UserEntity> findByUsernameNot(@Nonnull String username);

    @Query("select u from UserEntity u where u.username <> :username" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    List<UserEntity> findByUsernameNotAndSearchQuery(@Param("username") String username,
                                                     @Param("searchQuery") String searchQuery);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.ACCEPTED and f.requester = :requester")
    Slice<UserEntity> findFriends(@Param("requester") UserEntity requester,
                                  @Nonnull Pageable pageable);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.ACCEPTED and f.requester = :requester" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    Slice<UserEntity> findFriends(@Param("requester") UserEntity requester,
                                  @Nonnull Pageable pageable,
                                  @Param("searchQuery") String searchQuery);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.ACCEPTED and f.requester = :requester")
    List<UserEntity> findFriends(@Param("requester") UserEntity requester);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.ACCEPTED and f.requester = :requester" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    List<UserEntity> findFriends(@Param("requester") UserEntity requester,
                                 @Param("searchQuery") String searchQuery);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.requester = :requester")
    Slice<UserEntity> findOutcomeInvitations(@Param("requester") UserEntity requester,
                                             @Nonnull Pageable pageable);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.requester = :requester" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    Slice<UserEntity> findOutcomeInvitations(@Param("requester") UserEntity requester,
                                             @Nonnull Pageable pageable,
                                             @Param("searchQuery") String searchQuery);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.requester = :requester")
    List<UserEntity> findOutcomeInvitations(@Param("requester") UserEntity requester);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.addressee" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.requester = :requester" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    List<UserEntity> findOutcomeInvitations(@Param("requester") UserEntity requester,
                                            @Param("searchQuery") String searchQuery);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.requester" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.addressee = :addressee")
    Slice<UserEntity> findIncomeInvitations(@Param("addressee") UserEntity addressee,
                                            @Nonnull Pageable pageable);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.requester" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.addressee = :addressee" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    Slice<UserEntity> findIncomeInvitations(@Param("addressee") UserEntity addressee,
                                            @Nonnull Pageable pageable,
                                            @Param("searchQuery") String searchQuery);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.requester" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.addressee = :addressee")
    List<UserEntity> findIncomeInvitations(@Param("addressee") UserEntity addressee);

    @Query("select u from UserEntity u join FriendshipEntity f on u = f.requester" +
            " where f.status = guru.qa.rangiffler.entity.friendship.FriendshipStatus.PENDING and f.addressee = :addressee" +
            " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.lastname like %:searchQuery%)")
    List<UserEntity> findIncomeInvitations(@Param("addressee") UserEntity addressee,
                                           @Param("searchQuery") String searchQuery);
}
