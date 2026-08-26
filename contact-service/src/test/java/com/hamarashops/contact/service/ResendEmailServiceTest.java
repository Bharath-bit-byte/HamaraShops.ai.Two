package com.hamarashops.contact.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamarashops.contact.model.ContactInquiryRequest;
import com.hamarashops.contact.service.impl.ResendEmailServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResendEmailServiceTest {

    @Test
    public void testSendInquiryEmail() {
        ObjectMapper objectMapper = new ObjectMapper();
        ResendEmailServiceImpl service = new ResendEmailServiceImpl(objectMapper);

        ContactInquiryRequest request = new ContactInquiryRequest(
                "HamaraShops Tester",
                "gorantlacjaran14@gmail.com",
                "General Inquiries",
                "HamaraShops Test",
                "This is a test email from HamaraShops.ai."
        );

        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            String emailId = service.sendInquiryEmail(request);
            assertNotNull(emailId, "Resend Email ID should not be null when valid RESEND_API_KEY is provided.");
            System.out.println("Resend Test Email ID: " + emailId);
        } else {
            System.out.println("RESEND_API_KEY environment variable is not set. Skipping live Resend assertion.");
            String emailId = service.sendInquiryEmail(request);
            assertNull(emailId, "Should return null gracefully when RESEND_API_KEY is not set.");
        }
    }
}
