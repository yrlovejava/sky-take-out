package com.sky.service;

import com.sky.entity.AddressBook;
import java.util.List;

public interface AddressBookService {

    List<AddressBook> list(AddressBook addressBook);

    void save(AddressBook addressBook);

    AddressBook getById(String id);

    void update(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    void deleteById(String id);

}
