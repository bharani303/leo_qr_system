package com.example.qr_leo.service;

import com.example.qr_leo.model.qr_data;
import com.example.qr_leo.repo.qrrepo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class qrservice {

@Autowired
    qrrepo obj;

    public String addticket(qr_data val) throws Exception {

        // 1️⃣ Save ticket
        qr_data saved = obj.save(val);

        // 2️⃣ Get generated ID
        Integer generatedId = saved.getId();

        // 3️⃣ Generate QR using generated ID
        byte[] qrImage = generateQR(generatedId);

        // 4️⃣ Send Email with QR
        sendMail(saved.getEmail(), qrImage);

        return "Ticket Generated & Email Sent Successfully ✅";
    }
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
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, byte[] qrImage) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setFrom("brnprotripz1@gmail.com", "Leo Club");
        helper.setSubject("🌈 Leo Club Holi 2026 - Ticket Confirmation 🎟");

        String htmlContent = """
        <html>
            <body style="font-family: Arial, sans-serif; text-align:center;">
                <h1 style="color:#ff5722;">🌈 Leo Club Holi 2026 🎉</h1>
                
                <p style="font-size:16px;">
                    Dear Participant,
                </p>
                
                <p style="font-size:16px;">
                    Your ticket has been successfully confirmed!
                </p>

                <p style="font-size:16px;">
                    Please find your <strong>QR Code</strong> attached to this email.
                </p>

                <p style="font-size:16px;">
                    🎨 Get ready for colors, music, and unforgettable memories!
                </p>

                <br>

                <p style="font-size:14px; color:gray;">
                    📍 Venue: [Add Venue Here]<br>
                    📅 Date: [Add Date Here]<br>
                    ⏰ Time: [Add Time Here]
                </p>

                <br>

                <p style="font-weight:bold;">
                    Show this QR code at the entry gate.
                </p>

                <br><br>

                <p style="color:#555;">
                    Regards,<br>
                    Leo Club Team
                </p>
            </body>
        </html>
        """;

        helper.setText(htmlContent, true); // true = HTML content

        helper.addAttachment("LeoClub_Holi_Ticket.png",
                new ByteArrayResource(qrImage));

        mailSender.send(message);
    }








    public qr_data getting(int val) {
        return obj.findById(val).orElse(null);
    }

    public String putting(int id) {

        qr_data temp = obj.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid QR ID"));

        int tickets = temp.getTickets();

        if (tickets > 0) {
            tickets -= 1;
            temp.setTickets(tickets);

            if (tickets == 0) {
                temp.setValid(false);
            }

            obj.save(temp);   // 🔥 IMPORTANT (Save changes)

            return "Scanned Successfully";
        } else {
            temp.setValid(false);
            obj.save(temp);   // Save here also
            return "TICKET USED";
        }
    }
}
