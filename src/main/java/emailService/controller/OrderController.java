package emailService.controller;

import emailService.entity.Order;
import emailService.exception.CustomerLimitReachedException;
import emailService.exception.OverloadException;
import emailService.processing.OrderService;
import emailService.processing.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@RestController
public class OrderController {

    private final OrderService orderService;

    private final RequestValidator validator;

    @Autowired
    public OrderController(OrderService orderService, RequestValidator validator) {
        this.orderService = orderService;
        this.validator = validator;
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public void getOrder(@RequestBody Order order, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        if (!validator.valid(order)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            printWriter.write("Invalid JSON file.");
            return;
        }
        try {
            this.orderService.acceptOrder(order);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            printWriter.write("Your order is accepted.");
        } catch (OverloadException | CustomerLimitReachedException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            printWriter.write(e.getLocalizedMessage());
        }
    }

    @RequestMapping(value = "/set-max-volume")
    public String setMaxOrderVolume(@RequestParam(name = "count") Integer count) {
        this.orderService.setMaxOrderVolume(count);
        return "Maximum server order volume was changed to " + count;
    }

    @RequestMapping(value = "/refresh-senders-limit")
    public String refreshCustomerLimit(@RequestParam(name = "customer") String customer) {
        this.orderService.refreshCustomerLimit(customer);
        return customer + " limit per hour has been refreshed.";
    }
}
