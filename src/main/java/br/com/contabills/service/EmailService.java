package br.com.contabills.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envia um e-mail com um anexo.
     *
     * @param to      Endereço de destino
     * @param subject Assunto do e-mail
     * @param text    Mensagem do e-mail
     * @param file    Arquivo (PDF) para anexar
     * @throws MessagingException Em caso de erro no envio
     * @throws IOException
     */
    public void sendEmailWithAttachment(String to, String subject, String text, MultipartFile file)
            throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        if (!file.isEmpty()) {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes());
            helper.addAttachment(file.getOriginalFilename(), resource, file.getContentType());
        }

        mailSender.send(message);
    }
}
