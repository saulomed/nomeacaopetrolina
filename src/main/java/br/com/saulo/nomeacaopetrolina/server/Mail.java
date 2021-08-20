package br.com.saulo.nomeacaopetrolina.server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

public class Mail
{
    public static final String ASSUNTO_FALHA = "Verificação Nomeação Petrolina - %s";
    public static final String ASSUNTO_SUCESSO = "Possivel Nomeação - %s";
    private final Log LOGGER = LogFactory.getLog(Mail.class);

    public void enviaEmail(String texto, String nomeAssunto, String textoAssunto) {
        Properties props = new Properties();
        /** Parâmetros de conexão com servidor Gmail */
        props.put("mail.debug", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.1 TLSv1.2");
//        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication("nomeacaopetrolina@gmail.com",
                                "shak1234");
                    }
                });

        /** Ativa Debug para sessão */
        session.setDebug(true);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("seuemail@gmail.com"));
            //Remetente

            Address[] toUser = InternetAddress //Destinatário(s)
                    .parse("saulomed@gmail.com, lorenatablada@gmail.com");

            message.setRecipients(Message.RecipientType.TO, toUser);
            String assuntoEmail = textoAssunto;
            assuntoEmail = String.format(assuntoEmail,nomeAssunto);
            message.setSubject(assuntoEmail);//Assunto
            message.setText(texto);
            /**Método para enviar a mensagem criada*/
            Transport.send(message);

            System.out.println("Feito!!!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviaEmailFalha(String texto, String nomeAssunto)
    {
        enviaEmail(texto,nomeAssunto,ASSUNTO_FALHA);
    }
    public void enviaEmailSucesso(String texto, String nomeAssunto)
    {
        enviaEmail(texto,nomeAssunto,ASSUNTO_SUCESSO);
    }
}
