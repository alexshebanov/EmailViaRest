package emailService.processing;

import emailService.dao.OrderRepository;
import emailService.entity.Order;
import emailService.entity.OrderCharacteristics;
import emailService.entity.OrderContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class OrderService {

    private final int MAX_ORDERS_PER_HOUR_FOR_ONE_CUSTOMER = 3;
    private long MAX_ORDERS_VOLUME_FOR_EMAIL_SERVER = 1000;
    private long currentOrdersVolume = 0;

    private final OrderRepository orderRepository;
    private JavaMailSender mailSender;

    @Autowired
    public OrderService(JavaMailSender mailSender, OrderRepository orderRepository) {
        this.mailSender = mailSender;
        this.orderRepository = orderRepository;
    }

    public void send(Order order) throws MessagingException {
        int orderWeight = order.getContent().getValue().length() *
                (order.getTo().length + (order.getCc() != null ? order.getCc().length : 0)
                        + (order.getBcc() != null ? order.getBcc().length : 0));
        if (currentOrdersVolume + orderWeight > MAX_ORDERS_VOLUME_FOR_EMAIL_SERVER) {
            throw new MessagingException("Server is overloaded. Try later.");
        }
        String sender = order.getFrom();
        OrderCharacteristics orderCharacteristics;
        if (!orderRepository.existsBySender(sender))
            orderCharacteristics = new OrderCharacteristics(sender);
        else orderCharacteristics = orderRepository.findBySender(sender);
        if (orderCharacteristics.getOrdersCount() < MAX_ORDERS_PER_HOUR_FOR_ONE_CUSTOMER) {
            if (orderCharacteristics.getOrdersCount() == 0) {
                mailSender.send(preparedMessage(order));
                orderCharacteristics.setFirstSendDate(new Date());
                orderCharacteristics.incrementOrdersCount();
                currentOrdersVolume += orderWeight;
                orderRepository.save(orderCharacteristics);
            } else {
                mailSender.send(preparedMessage(order));
                orderCharacteristics.incrementOrdersCount();
                currentOrdersVolume += orderWeight;
                orderRepository.save(orderCharacteristics);
            }
        } else if (new Date().getTime() - orderCharacteristics.getFirstSendDate().getTime()
                < 3600000) {
            SimpleDateFormat format = new SimpleDateFormat("mm 'minutes' ss 'seconds'");
            throw new MessagingException("Yo've reached " +
                    "order hour limit. Try in " + format.format(3600000 - new Date().getTime() +
                    orderCharacteristics.getFirstSendDate().getTime()));
        } else {
            mailSender.send(preparedMessage(order));
            orderCharacteristics.setFirstSendDate(new Date());
            orderCharacteristics.setOrdersCount(1);
            currentOrdersVolume += orderWeight;
            orderRepository.save(orderCharacteristics);
        }
    }

    private MimeMessage preparedMessage(Order order) throws MessagingException {
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
        return message;
    }

    public void refreshServer() {
        this.currentOrdersVolume = 0;
    }

    public void setMaxOrderVolume(Integer count) {
        this.MAX_ORDERS_VOLUME_FOR_EMAIL_SERVER = count;
    }

    public int maxOrdersForCustomer() {
        return MAX_ORDERS_PER_HOUR_FOR_ONE_CUSTOMER;
    }

    public void refreshCustomerLimit(String sender) {
        OrderCharacteristics orderCharacteristics;
        if (!orderRepository.existsBySender(sender))
            return;
        else orderCharacteristics = orderRepository.findBySender(sender);
        orderCharacteristics.setOrdersCount(0);
        orderRepository.save(orderCharacteristics);
    }
}
