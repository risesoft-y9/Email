package net.risesoft.james.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.james.entity.JamesUser;

@Repository
public interface JamesUserRepository extends JpaRepository<JamesUser, String>, JpaSpecificationExecutor<JamesUser> {

    @Query("select emailAddress from JamesUser where personId=?1")
    String findEmailAddressByPersonId(String personId);

    @Query("select emailAddress from JamesUser where personId in (?1)")
    List<String> findEmailAddressByPersonIds(List<String> ids);

    Optional<JamesUser> findByPersonId(String personId);

    Optional<JamesUser> findByEmailAddress(String emailAddress);

    @Transactional(readOnly = false)
    @Modifying
    void deleteByPersonId(String personId);
}
