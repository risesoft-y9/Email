package net.risesoft.james.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.james.entity.JamesAddressBook;

public interface JamesAddressBookRepository
    extends JpaRepository<JamesAddressBook, String>, JpaSpecificationExecutor<JamesAddressBook> {

    List<JamesAddressBook> findByPersonIdAndEmailAddressLikeOrNameLikeOrderByNameAsc(String personId,
        String emailAddress, String name);

    List<JamesAddressBook> findByPersonIdOrderByNameAsc(String personId);

}
