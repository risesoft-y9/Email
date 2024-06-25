package net.risesoft.james.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.james.entity.JamesAddressBook;

public interface JamesAddressBookRepository
    extends JpaRepository<JamesAddressBook, String>, JpaSpecificationExecutor<JamesAddressBook> {

    @Query("from JamesAddressBook a where a.personId=?1 and (a.emailAddress like  %?2% or a.name like %?3%) order by a.name ASC")
    List<JamesAddressBook> findByPersonIdAndEmailAddressLikeOrNameLikeOrderByNameAsc(String personId,
        String emailAddress, String name);

    List<JamesAddressBook> findByPersonIdOrderByNameAsc(String personId);

}
