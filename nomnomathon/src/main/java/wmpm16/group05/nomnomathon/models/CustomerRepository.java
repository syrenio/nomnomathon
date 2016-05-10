package wmpm16.group05.nomnomathon.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by syrenio on 5/10/2016.
 */
@Repository
public interface CustomerRepository extends CrudRepository<Customer,Long> {

    Customer findOneByPhoneNumber(String phoneNumber);
}
