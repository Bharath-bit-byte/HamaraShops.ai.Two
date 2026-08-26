package com.hamarashops.contact.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamarashops.contact.exception.ResourceNotFoundException;
import com.hamarashops.contact.model.ContactInfo;
import com.hamarashops.contact.model.ContactInquiryRequest;
import com.hamarashops.contact.model.ContactInquiryResponse;
import com.hamarashops.contact.service.ContactService;
import com.hamarashops.contact.service.ResendEmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final ResendEmailService resendEmailService;

    private ContactInfo contactInfo;

    public ContactServiceImpl(ResourceLoader resourceLoader, ObjectMapper objectMapper, ResendEmailService resendEmailService) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.resendEmailService = resendEmailService;
    }

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource("classpath:data/contact-info.json");
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    this.contactInfo = objectMapper.readValue(inputStream, ContactInfo.class);
                }
            }
        } catch (Exception e) {
            this.contactInfo = new ContactInfo();
        }
    }

    @Override
    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }

    @Override
    public ContactInquiryResponse submitInquiry(ContactInquiryRequest request) {
        if (request == null || request.getFullName() == null || request.getFullName().trim().isEmpty()
                || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name and email are required for inquiry submission.");
        }

        if (request.getCategory() != null && !request.getCategory().trim().isEmpty() && contactInfo != null && contactInfo.getInquiryCategories() != null) {
            boolean validCategory = contactInfo.getInquiryCategories().stream()
                    .anyMatch(cat -> cat.equalsIgnoreCase(request.getCategory().trim()));
            if (!validCategory) {
                throw new ResourceNotFoundException("Invalid inquiry category: " + request.getCategory() + ". Approved categories: " + contactInfo.getInquiryCategories());
            }
        }

        String inquiryId = "inq-" + UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().toString();

        String resendEmailId = resendEmailService.sendInquiryEmail(request);

        String receiptMessage = resendEmailId != null
                ? "Inquiry received and notification email dispatched via Resend (" + resendEmailId + ")."
                : "Inquiry received for validation. Thank you for contacting HamaraShops.ai.";

        return new ContactInquiryResponse(inquiryId, "RECEIVED", timestamp, receiptMessage, resendEmailId);
    }
}
