package net.risesoft.james.service;

import java.util.List;

import net.risesoft.james.entity.JamesAddressBook;

public interface JamesAddressBookService {

    JamesAddressBook saveOrUpdate(JamesAddressBook jamesAddressBook);

    void delete(String id);

    JamesAddressBook findOne(String id);

    List<JamesAddressBook> findSearch(String search);

    List<JamesAddressBook> findAll();

}
