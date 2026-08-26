package com.hamarashops.contact.service;

import com.hamarashops.contact.model.ContactInquiryRequest;

public interface ResendEmailService {
    String sendInquiryEmail(ContactInquiryRequest request);
}
