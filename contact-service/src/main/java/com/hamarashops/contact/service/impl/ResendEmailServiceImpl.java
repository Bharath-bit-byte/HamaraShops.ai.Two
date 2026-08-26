package com.hamarashops.contact.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamarashops.contact.model.ContactInquiryRequest;
import com.hamarashops.contact.service.ResendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResendEmailServiceImpl implements ResendEmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailServiceImpl.class);
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @Value("${RESEND_API_KEY:${resend.api.key:}}")
    private String resendApiKey;

    @Value("${CONTACT_RECIPIENT:${contact.recipient:gorantlacjaran14@gmail.com}}")
    private String contactRecipient;

    @Value("${RESEND_FROM:${resend.from:onboarding@resend.dev}}")
    private String resendFrom;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ResendEmailServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String sendInquiryEmail(ContactInquiryRequest request) {
        String apiKey = (resendApiKey != null && !resendApiKey.trim().isEmpty())
                ? resendApiKey.trim()
                : System.getenv("RESEND_API_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("RESEND_API_KEY environment variable is not configured. Skipping Resend email dispatch.");
            return null;
        }

        String recipient = (contactRecipient != null && !contactRecipient.trim().isEmpty())
                ? contactRecipient.trim()
                : (System.getenv("CONTACT_RECIPIENT") != null ? System.getenv("CONTACT_RECIPIENT").trim() : "gorantlacjaran14@gmail.com");

        String from = (resendFrom != null && !resendFrom.trim().isEmpty())
                ? resendFrom.trim()
                : (System.getenv("RESEND_FROM") != null ? System.getenv("RESEND_FROM").trim() : "onboarding@resend.dev");

        String subject = (request != null && request.getSubject() != null && !request.getSubject().trim().isEmpty())
                ? request.getSubject().trim()
                : "HamaraShops Test";

        String messageText = (request != null && request.getMessage() != null && !request.getMessage().trim().isEmpty())
                ? request.getMessage().trim()
                : "This is a test email from HamaraShops.ai.";

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", from);
            payload.put("to", Collections.singletonList(recipient));
            payload.put("subject", subject);
            payload.put("text", messageText);
            
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">"
                    + "<h2 style=\"color: #0a1628;\">HamaraShops.ai Inquiry Notification</h2>"
                    + "<p>" + messageText + "</p>"
                    + "<hr style=\"border: 1px solid #eee;\"/>"
                    + (request != null ? "<p><b>Full Name:</b> " + (request.getFullName() != null ? request.getFullName() : "N/A") + "</p>" : "")
                    + (request != null ? "<p><b>Sender Email:</b> " + (request.getEmail() != null ? request.getEmail() : "N/A") + "</p>" : "")
                    + (request != null && request.getCategory() != null ? "<p><b>Category:</b> " + request.getCategory() + "</p>" : "")
                    + "</div>";
            payload.put("html", htmlContent);

            String jsonPayload = objectMapper.writeValueAsString(payload);

            log.info("Dispatching email via Resend API to: {} with subject: '{}'", recipient, subject);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_API_URL))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .timeout(Duration.ofSeconds(15))
            .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("Resend API HTTP status: {}", httpResponse.statusCode());
            log.info("Resend API response payload: {}", httpResponse.body());

            if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                Map<?, ?> responseMap = objectMapper.readValue(httpResponse.body(), Map.class);
                Object emailIdObj = responseMap.get("id");
                if (emailIdObj != null) {
                    String emailId = emailIdObj.toString();
                    log.info("Resend email request accepted successfully! Resend Email ID: {}", emailId);
                    return emailId;
                }
            } else {
                log.error("Resend API error status: {}, body: {}", httpResponse.statusCode(), httpResponse.body());
            }
        } catch (Exception e) {
            log.error("Exception during Resend email dispatch: {}", e.getMessage(), e);
        }

        return null;
    }
}
