package com.example.qr_leo.service;

import com.example.qr_leo.model.qr_data;
import com.example.qr_leo.repo.qrrepo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.resend.services.emails.model.Attachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class qrservice {

    @Autowired
    private qrrepo obj;

    @Value("${resend.api.key}")
    private String resendApiKey;

    // ==============================
    // ADD TICKET
    // ==============================
    public String addticket(qr_data val) {

        try {

            qr_data saved = obj.save(val);

            byte[] qrImage = generateQR(saved.getId());

            boolean emailSent = sendMail(saved.getEmail(), qrImage);

            if (emailSent) {
                return "Ticket Generated & Email Sent Successfully ✅";
            } else {
                return "Ticket Saved But Email Failed ⚠";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong ❌";
        }
    }

    // ==============================
    // QR GENERATOR
    // ==============================
    public byte[] generateQR(Integer id) throws Exception {

        String text = "https://leo-holi-gateway.netlify.app/" + id;

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
    // SEND EMAIL (RESEND API)
    // ==============================
    public boolean sendMail(String to, byte[] qrImage) {

        try {

            String base64QR = Base64.getEncoder().encodeToString(qrImage);

            Resend resend = new Resend(resendApiKey);

            Attachment attachment = Attachment.builder()
                    .fileName("LeoClub_Ticket.png")   // ✅ correct method
                    .content(base64QR)
                    .build();

            CreateEmailOptions email = CreateEmailOptions.builder()
                    // 🔁 CHANGE THIS AFTER DOMAIN VERIFICATION
                    .from("Leo Club <noreply@aurainfotechsolutions.live>")
                    // After verify:
                    // .from("Leo Club <noreply@aurainfotechsolutions.live>")
                    .to(to)
                    .subject(" Leo Club Holi 2026 - Ticket Confirmation 🎟")
                    .html(buildHtmlTemplate())
                    .attachments(List.of(attachment))
                    .build();

            CreateEmailResponse response = resend.emails().send(email);

            System.out.println("Email Sent Successfully. ID: " + response.getId());

            return true;

        } catch (Exception e) {
            System.out.println("Resend Email Failed: " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // EMAIL HTML TEMPLATE
    // ==============================
    private String buildHtmlTemplate() {

        return """
            <div style='font-family: Arial, sans-serif; padding:20px; background-color:#f4f4f4;'>

                <div style='max-width:600px; margin:auto; background:white; padding:30px; border-radius:12px;'>

                    <h1 style='color:#ff4d6d; text-align:center;'> Leo Club Holi 2026 🎉</h1>

                    <p>Dear Participant,</p>

                    <p>Your ticket has been <b style='color:green;'>successfully confirmed!</b> 🎟</p>

                    <hr/>

                    <h2>📅 Event Details</h2>

                    <p><b>Date:</b> 8/3/2025</p>
                    <p><b>Time:</b> 3:00 PM – 8:00 PM</p>
                    <p><b>Venue:</b> SRK Miraj Cinemas Theater</p>
                    <p><b>Landmark:</b> Opposite Prasanna Groups</p>

                    <p>
                        <a href='https://maps.app.goo.gl/H3BKmPwXwwwidbxR9'>
                        View on Google Maps 📍
                        </a>
                    </p>

                    <hr/>

                    <p>Please show the attached QR code at entry.</p>

                    <p>Get ready for colors, music, dance & unlimited fun! 🌸🎶</p>

                    <p style='margin-top:30px;'>
                        Regards,<br>
                        <b>Leo Club Team</b>
                    </p>

                </div>
            </div>
        """;
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
            return "TICKET USED !";
        }
    }
List<qr_data> list=new ArrayList<>();
    public String getTotal() {
        list=obj.findAll();
        long a=0;
        for(qr_data x:list){
            a+=Long.parseLong(x.getAmount());
        }
      return  String.valueOf(a);
    }

    public List<qr_data> getdatabase() {
    return obj.findAll();
    }
}