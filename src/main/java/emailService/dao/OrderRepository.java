package emailService.dao;

import emailService.entity.OrderCharacteristics;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderCharacteristics, Long> {
    OrderCharacteristics findBySender(String sender);

    @Query("select case when count(e) > 0 then true else false end from OrderCharacteristics e" +
            " where e.sender = ?1")
    public Boolean existsBySender(String sender);
}
