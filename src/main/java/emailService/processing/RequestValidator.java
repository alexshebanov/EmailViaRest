package emailService.processing;

import emailService.entity.Order;
import emailService.entity.OrderContent;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {
    public boolean valid(Order order) {
        return !(order.getFrom() == null || order.getContent().getType() == null ||
                order.getContent().getValue() == null ||
                order.getTo() == null || order.getSubject() == null) &&
                (order.getContent().getType().equals(OrderContent.TEXT_HTML_TYPE) ||
                        order.getContent().getType().equals(OrderContent.TEXT_PLAIN_TYPE));
    }
}
