package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminContactUpsertRequest;
import com.fluffyletter.dto.ContactDTO;
import com.fluffyletter.service.AuthHeaderService;
import com.fluffyletter.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/contact")
public class AdminContactController {

    private final ContactService contactService;
    private final AuthHeaderService authHeaderService;

    public AdminContactController(ContactService contactService, AuthHeaderService authHeaderService) {
        this.contactService = contactService;
        this.authHeaderService = authHeaderService;
    }

    @GetMapping
    public ContactDTO get(@RequestHeader("Authorization") String authorization) {
        authHeaderService.requireAdmin(authorization);
        return contactService.get();
    }

    @PutMapping
    public ContactDTO upsert(@RequestHeader("Authorization") String authorization,
                             @Valid @RequestBody AdminContactUpsertRequest request) {
        authHeaderService.requireAdmin(authorization);
        return contactService.update(new ContactDTO(request.getWechatId(), request.getQrcodeUrl()));
    }
}
