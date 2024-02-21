package net.risesoft.james.service;

import net.risesoft.james.entity.JamesAddressBook;

import java.util.List;

public interface JamesAddressBookService {

    JamesAddressBook saveOrUpdate(JamesAddressBook jamesAddressBook);

    void delete(String id);

    JamesAddressBook findOne(String id);

    List<JamesAddressBook> findSearch(String search);

    List<JamesAddressBook> findAll();

}
