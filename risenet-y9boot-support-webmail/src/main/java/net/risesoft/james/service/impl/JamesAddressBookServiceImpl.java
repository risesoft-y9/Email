package net.risesoft.james.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.james.entity.JamesAddressBook;
import net.risesoft.james.repository.JamesAddressBookRepository;
import net.risesoft.james.service.JamesAddressBookService;
import net.risesoft.y9.Y9LoginUserHolder;

@Service(value = "jamesAddressBookService")
public class JamesAddressBookServiceImpl implements JamesAddressBookService {

    @Autowired
    private JamesAddressBookRepository jamesAddressBookRepository;

    @Override
    public JamesAddressBook saveOrUpdate(JamesAddressBook jamesAddressBook) {
        if (StringUtils.isBlank(jamesAddressBook.getId())) {
            jamesAddressBook
                .setId(Y9LoginUserHolder.getUserInfo().getPersonId() + "-" + jamesAddressBook.getEmailAddress());
        }
        jamesAddressBook.setPersonId(Y9LoginUserHolder.getUserInfo().getPersonId());
        return jamesAddressBookRepository.save(jamesAddressBook);
    }

    @Override
    public void delete(String id) {
        if (jamesAddressBookRepository.findById(id).orElse(null) != null)
            jamesAddressBookRepository.deleteById(id);
    }

    @Override
    public JamesAddressBook findOne(String id) {
        return jamesAddressBookRepository.findById(id).orElse(null);
    }

    @Override
    public List<JamesAddressBook> findSearch(String search) {
        // return jamesAddressBookRepository.findSearch(Y9LoginUserHolder.getUserInfo().getPersonId(),"%"+search+"%");
        return jamesAddressBookRepository.findByPersonIdAndEmailAddressLikeOrNameLikeOrderByNameAsc(
            Y9LoginUserHolder.getUserInfo().getPersonId(), "%" + search + "%", "%" + search + "%");
    }

    @Override
    public List<JamesAddressBook> findAll() {
        return jamesAddressBookRepository.findByPersonIdOrderByNameAsc(Y9LoginUserHolder.getUserInfo().getPersonId());
    }
}
