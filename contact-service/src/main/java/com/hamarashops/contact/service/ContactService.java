package com.hamarashops.contact.service;

import com.hamarashops.contact.model.ContactInfo;
import com.hamarashops.contact.model.ContactInquiryRequest;
import com.hamarashops.contact.model.ContactInquiryResponse;

public interface ContactService {
    ContactInfo getContactInfo();
    ContactInquiryResponse submitInquiry(ContactInquiryRequest request);
}
