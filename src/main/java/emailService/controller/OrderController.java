package emailService.controller;

import emailService.entity.Order;
import emailService.processing.OrderService;
import emailService.processing.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
            printWriter.write("Invalid JSON file");
        }
        try {
            this.orderService.send(order);
            response.setStatus(HttpServletResponse.SC_OK);
            printWriter.write("Success");
        } catch (MessagingException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            printWriter.write(e.getLocalizedMessage());
        }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public String refreshServer() {
        this.orderService.refreshServer();
        return "Current volume has been refreshed.";
    }

    @RequestMapping(value = "/set-volume")
    public String setMaxOrderVolume(@RequestParam(name = "count", required = true) Integer count) {
        this.orderService.setMaxOrderVolume(count);
        return "Maximum server order volume was changed to " + count;
    }

    @RequestMapping(value = "/refresh-senders-limit")
    public String refreshCustomerLimit(@RequestParam(name = "customer", required = true) String customer) {
        this.orderService.refreshCustomerLimit(customer);
        return customer + " limit per hour has been refreshed.";
    }
}
