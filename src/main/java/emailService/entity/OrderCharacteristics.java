package emailService.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class OrderCharacteristics {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String sender;

    private Date firstSendDate;

    public OrderCharacteristics() {
    }

    public OrderCharacteristics(String sender) {
        this.sender = sender;
        this.ordersCount = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private int ordersCount = 0;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getFirstSendDate() {
        return firstSendDate;
    }

    public void setFirstSendDate(Date firstSendDate) {
        this.firstSendDate = firstSendDate;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    public void incrementOrdersCount() {
        ordersCount++;
    }
}
