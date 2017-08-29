package emailService.processing;

import emailService.dao.OrderRepository;
import emailService.entity.Order;
import emailService.entity.OrderCharacteristics;
import emailService.entity.OrderContent;
import emailService.exception.CustomerLimitReachedException;
import emailService.exception.OverloadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderService {

    private final int MAX_ORDERS_PER_HOUR_FOR_ONE_CUSTOMER = 5;
    /*custom value, can be changed by '/set-max-volume'  GET request*/
    private long MAX_ORDERS_VOLUME_FOR_EMAIL_SERVER = 100000000;
    final static AtomicLong summaryOrdersVolume = new AtomicLong(0);

    private final OrderRepository orderRepository;
    private EMailSender mailSender;

    @Autowired
    public OrderService(EMailSender mailSender, OrderRepository orderRepository) {
        this.mailSender = mailSender;
        this.orderRepository = orderRepository;
    }

    public void acceptOrder(Order order) throws OverloadException, CustomerLimitReachedException {
        /*Checking order's volume*/
        int orderWeight = order.getContent().getValue().length() *
                (order.getTo().length + (order.getCc() != null ? order.getCc().length : 0)
                        + (order.getBcc() != null ? order.getBcc().length : 0));
        if (summaryOrdersVolume.get() + orderWeight > MAX_ORDERS_VOLUME_FOR_EMAIL_SERVER) {
            throw new OverloadException("Service is overloaded. Try later.");
        }

        String sender = order.getFrom();
        OrderCharacteristics orderCharacteristics;
        if (!orderRepository.existsBySender(sender))
            orderCharacteristics = new OrderCharacteristics(sender);
        else orderCharacteristics = orderRepository.findBySender(sender);

        /*Validation of customers limit per hour*/
        if (orderCharacteristics.getOrdersCount() < MAX_ORDERS_PER_HOUR_FOR_ONE_CUSTOMER) {
            if (orderCharacteristics.getOrdersCount() == 0)
                orderCharacteristics.setFirstSendDate(new Date());
            orderCharacteristics.incrementOrdersCount();
            summaryOrdersVolume.addAndGet(orderWeight);
            orderRepository.save(orderCharacteristics);
            try {
                mailSender.send(order, orderWeight);
            } catch (MessagingException e) {
                summaryOrdersVolume.addAndGet(-orderWeight);
            }
        } else if (new Date().getTime() - orderCharacteristics.getFirstSendDate().getTime()
                < 3600000) {
            SimpleDateFormat format = new SimpleDateFormat("mm 'minutes' ss 'seconds'");
            throw new CustomerLimitReachedException("Yo've reached " +
                    "order hour limit. Try in " + format.format(3600000 - new Date().getTime() +
                    orderCharacteristics.getFirstSendDate().getTime()));
        } else {
            orderCharacteristics.setFirstSendDate(new Date());
            orderCharacteristics.setOrdersCount(1);
            summaryOrdersVolume.addAndGet(orderWeight);
            orderRepository.save(orderCharacteristics);
            try {
                mailSender.send(order, orderWeight);
            } catch (MessagingException e) {
                summaryOrdersVolume.addAndGet(-orderWeight);
            }
        }
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
