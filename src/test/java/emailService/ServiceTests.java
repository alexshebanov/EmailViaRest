package emailService;

import emailService.entity.Order;
import emailService.entity.OrderContent;
import emailService.processing.OrderService;
import emailService.processing.RequestValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTests {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RequestValidator validator;

    private final String[] mail = {"abc@mail.ru", "boo@gmail.com", "foo@bar.ua"};
    private final String[] anotherMail = {"another@mail.com"};

    private Order correctOrder() {
        Order order = new Order();
        OrderContent content = new OrderContent();
        content.setType(OrderContent.TEXT_PLAIN_TYPE);
        content.setValue("hello");
        order.setFrom("sender@gmail.com");
        order.setTo(anotherMail);
        order.setCc(mail);
        order.setBcc(null);
        order.setContent(content);
        order.setSubject("subject");
        return order;
    }

    @Test
    public void validationOfCorrectOrder() {
        Order order = correctOrder();
        assert validator.valid(order);
    }

    @Test
    public void recipientMissing() {
        Order order = correctOrder();
        order.setTo(null);
        assert !validator.valid(order);
    }

    @Test
    public void senderMissing() {
        Order order = correctOrder();
        order.setFrom(null);
        assert !validator.valid(order);
    }

    @Test
    public void subjectMissing() {
        Order order = correctOrder();
        order.setSubject(null);
        assert !validator.valid(order);
    }

    @Test
    public void wrongContentType() {
        Order order = correctOrder();
        order.getContent().setType("KONTENT TAIP");
        assert !validator.valid(order);
    }

    @Test
    public void orderSent() throws MessagingException {
        Order order = correctOrder();
        order.setFrom("sender@mail.ua");
        orderService.refreshCustomerLimit("sender@mail.ua");
        for (int i = 0; i < orderService.maxOrdersForCustomer(); i++)
            orderService.send(order);
    }

    @Test(expected = MessagingException.class)
    public void limitForOneCustomerReached() throws MessagingException {
        Order order = correctOrder();
        order.setFrom("anothersender@mail.ua");
        orderService.refreshCustomerLimit("anothersender@mail.ua");
        for (int i = 0; i < orderService.maxOrdersForCustomer() + 1; i++)
            orderService.send(order);
    }
}
