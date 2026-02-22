package com.example.qr_leo.service;

import com.example.qr_leo.model.qr_data;
import com.example.qr_leo.repo.qrrepo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class qrservice {

    @Autowired
    qrrepo obj;

    // ==============================
    // ADD TICKET
    // ==============================
    public String addticket(qr_data val) {

        try {



            // 1️⃣ Save ticket
            qr_data saved = obj.save(val);

            // 2️⃣ Generate QR
            Integer generatedId = saved.getId();
            byte[] qrImage = generateQR(generatedId);

            // 3️⃣ Send Email via MailerSend
            sendMail(saved.getEmail(), qrImage);

            return "Ticket Generated & Email Sent Successfully ✅";

        } catch (Exception e) {
            e.printStackTrace();
            return "Ticket Saved But Email Failed ⚠";
        }
    }

    // ==============================
    // QR GENERATOR
    // ==============================
    public byte[] generateQR(Integer id) throws Exception {

        String text = "Ticket-ID-" + id;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);

        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

        return baos.toByteArray();
    }

    // ==============================
    // SEND EMAIL (MAILERSEND HTTP)
    // ==============================
    @Value("${mail.api.key}")
    private String apiKy;
    public void sendMail(String to, byte[] qrImage) {

        try {

            String apiKey =  apiKy;

            String base64QR = Base64.getEncoder().encodeToString(qrImage);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> email = new HashMap<>();

            // MUST VERIFY THIS EMAIL IN MAILERSEND
            Map<String, String> from = new HashMap<>();
            from.put("email", "noreply@test-65qngkdm2y8lwr12.mlsender.net");
            from.put("name", "Leo Club");

            Map<String, String> toUser = new HashMap<>();
            toUser.put("email", to);
            toUser.put("name", "Participant");

            Map<String, String> attachment = new HashMap<>();
            attachment.put("filename", "LeoClub_Ticket.png");
            attachment.put("content", base64QR);

            email.put("from", from);
            email.put("to", List.of(toUser));
            email.put("subject", "🌈 Leo Club Holi 2026 - Ticket Confirmation 🎟");
            email.put("html",
                    "<h1>🌈 Leo Club Holi 2026 🎉</h1>" +
                            "<p>Your ticket has been successfully confirmed.</p>" +
                            "<p>Please show the attached QR code at entry.</p>"
            );
            email.put("attachments", List.of(attachment));

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(email, headers);

            restTemplate.postForEntity(
                    "https://api.mailersend.com/v1/email",
                    request,
                    String.class
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // GET TICKET
    // ==============================
    public qr_data getting(int val) {
        return obj.findById(val).orElse(null);
    }

    // ==============================
    // SCAN TICKET
    // ==============================
    public String putting(int id) {

        qr_data temp = obj.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid QR ID"));

        if (temp.getTickets() > 0) {
            temp.setTickets(temp.getTickets() - 1);
            obj.save(temp);
            return "Scanned Successfully";
        } else {
            return "TICKET USED";
        }
    }
}