package emailService.processing;

import emailService.entity.Order;
import emailService.entity.OrderContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EMailSender {
    private final JavaMailSender mailSender;

    @Autowired
    public EMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(Order order, int orderWeight) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(order.getFrom());
        if (order.getReply_to() != null)
            helper.setReplyTo(order.getReply_to());
        helper.setTo(order.getTo());
        if (order.getCc() != null)
            helper.setCc(order.getCc());
        if (order.getBcc() != null)
            helper.setBcc(order.getBcc());
        helper.setSubject(order.getSubject());
        if (order.getContent().getType().equals(OrderContent.TEXT_HTML_TYPE))
            helper.setText(order.getContent().getValue(), true);
        else helper.setText(order.getContent().getValue(), false);
        this.mailSender.send(message);
        OrderService.summaryOrdersVolume.addAndGet(-orderWeight);
    }
}
