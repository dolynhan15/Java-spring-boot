package com.example.swaggerbasic;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApiOperation(value = "profile/v1/book", tags = "Book Profile Controller")
@RestController
@RequestMapping("/api")
public class AddressBookResource {
    ConcurrentMap<String, Contact> contacts = new ConcurrentHashMap<>();

    @ApiOperation(value = "Fetch All Books", response = Iterable.class)

    @GetMapping("/")
    public List<Contact> getAllContacts() {
        return new ArrayList<Contact>(contacts.values());
    }

    @ApiOperation(value = "Get Book By Id", response = Iterable.class)
    @GetMapping("/{id}")
    public Contact getContact(@PathVariable String id) {
        return contacts.get(id);
    }

    @ApiOperation(value = "Insert Book Record", response = Iterable.class)
    @PostMapping("/")
    public Contact addContact(@RequestBody Contact contact) {
        contacts.put(contact.getId(), contact);
        return contact;

    }
}
