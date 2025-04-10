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

/**
 * Serviço responsável por envio de e-mails com ou sem anexos.
 * Utiliza o {@link JavaMailSender} para criar e enviar mensagens do tipo MIME.
 *
 * Atualmente, suporta envio de e-mail com anexo (PDF ou outro tipo de arquivo).
 * 
 * @author Gerson
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Construtor padrão do serviço de e-mail.
     */
    public EmailService() {
        // Construtor padrão necessário para frameworks e ferramentas de análise
    }

    /**
     * Envia um e-mail com um anexo.
     *
     * @param to      Endereço de destino
     * @param subject Assunto do e-mail
     * @param text    Mensagem do e-mail (pode conter HTML)
     * @param file    Arquivo (PDF ou outro tipo) para anexar
     * @throws MessagingException Em caso de erro no envio.
     * @throws IOException Se ocorrer um erro ao carregar o conteúdo do arquivo.
     */
    public void sendEmailWithAttachment(String to, String subject, String text, MultipartFile file)
            throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // true = conteúdo HTML

        if (!file.isEmpty()) {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes());
            helper.addAttachment(file.getOriginalFilename(), resource, file.getContentType());
        }

        mailSender.send(message);
    }
}
