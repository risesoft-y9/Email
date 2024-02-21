package net.risesoft.james.repository;

import net.risesoft.james.entity.JamesAddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JamesAddressBookRepository extends JpaRepository<JamesAddressBook, String>, JpaSpecificationExecutor<JamesAddressBook> {

    List<JamesAddressBook> findByPersonIdAndEmailAddressLikeOrNameLikeOrderByNameAsc(String personId,String emailAddress,String name);

    List<JamesAddressBook> findByPersonIdOrderByNameAsc(String personId);

}
