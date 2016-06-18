package wmpm16.group05.nomnomathon.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.*;

@Component
public class DatabaseSeeder {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    DishRepository dishRepository;
    @Autowired
    OrderInProcessRepository orderRepository;

    public void seed(){

        customerRepository.deleteAll();
        dishRepository.deleteAll();
        orderRepository.deleteAll();

        //authcode: YmVybmQ6bm9tbm9t
        Customer customer = new Customer("bernd","bernd","test","nomnom");
        customer.setPhoneNumber("+4368012345678");
        customer.setMail("a.b@c.d");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("Percostraße 27, 1220 Wien");
        customer.setCreditCard("5487765682447742");
        customerRepository.save(customer);

        //authcode: Ym11OjEyMzQ1
        customer = new Customer("bmu","Bernhard","Müller","12345");
        customer.setPhoneNumber("+43699812579");
        customer.setMail("bernhard.mueller@gmx.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("Percostraße 25, 1220 Wien");
        customer.setCreditCard("4411948396385770");
        customerRepository.save(customer);

        //authcode: bWF3ZToxMjM0NQ==
        customer = new Customer("mawe","Martin","Weik","12345");
        customer.setPhoneNumber("+4369912345678");
        customer.setMail("martin@weik.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("blabla, 1234 Wien");
        customer.setCreditCard("342503083304998");
        customerRepository.save(customer);

        //authcode: ZnJhbms6bm9tb25leQ==
        customer = new Customer("frank","frank","nomoney","nomoney");
        customer.setPhoneNumber("+4368000000000");
        customer.setMail("a.b@c.d");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("No Name Street 123");
        customer.setCreditCard("6011671952963165");
        customerRepository.save(customer);
    }
}
