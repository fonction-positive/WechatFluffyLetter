package com.fluffyletter.controller;

import com.fluffyletter.dto.ContactDTO;
import com.fluffyletter.service.ContactService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ApiContactController {

    private final ContactService contactService;

    public ApiContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ContactDTO get() {
        return contactService.get();
    }
}
