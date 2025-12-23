package com.fluffyletter.service;

import com.fluffyletter.dto.ContactDTO;
import com.fluffyletter.entity.ContactInfo;
import com.fluffyletter.repository.ContactInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactInfoRepository contactInfoRepository;

    public ContactService(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    public ContactDTO get() {
        ContactInfo info = contactInfoRepository.findById(1L).orElseGet(() -> {
            ContactInfo c = new ContactInfo();
            c.setId(1L);
            c.setWechatId("");
            c.setQrcodeUrl("");
            return contactInfoRepository.save(c);
        });
        return new ContactDTO(info.getWechatId(), info.getQrcodeUrl());
    }

    public ContactDTO update(ContactDTO dto) {
        ContactInfo info = contactInfoRepository.findById(1L).orElseGet(() -> {
            ContactInfo c = new ContactInfo();
            c.setId(1L);
            return c;
        });
        info.setWechatId(dto.getWechatId());
        info.setQrcodeUrl(dto.getQrcodeUrl());
        ContactInfo saved = contactInfoRepository.save(info);
        return new ContactDTO(saved.getWechatId(), saved.getQrcodeUrl());
    }
}
