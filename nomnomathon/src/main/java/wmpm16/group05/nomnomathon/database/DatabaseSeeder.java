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

        // mail regex = [a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}
        // e.g. a@b.c.at <- length of TLD at least 2

        // phoneNumber regex = \\+[0-9.]{5,}
        // strat with + add at least 5 numbers
        
        
        //authcode: YmVybmQ6bm9tbm9t
        Customer customer = new Customer("bernd","bernd","test","nomnom");
        customer.setPhoneNumber("+4368012345678");
        customer.setMail("a.b@c.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);
        customer.setAddress("Percostraße 27, 1220 Wien");
        customer.setCreditCard("5487765682447742");
        customerRepository.save(customer);

        //authcode: Ym11OjEyMzQ1
        customer = new Customer("bmu","Bernhard","Müller","12345");
        customer.setPhoneNumber("+0699923923293");
        customer.setMail("a.b@c.at");
        customer.setNotificationType(CustomerNotificationType.REST);
        customer.setAddress("Percostraße 25, 1220 Wien");
        customer.setCreditCard("4411948396385770");
        customerRepository.save(customer);

        
        //authcode: aG11OjEyMzQ1
        customer = new Customer("hmu","Hansi","Müller","12345");
        customer.setPhoneNumber("+0699923923434");
        customer.setMail("a.b@c.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);
        customer.setAddress("Percostraße 25, 1220 Wien");
        customer.setCreditCard("4411948396385770");
        customerRepository.save(customer);      
        
        //authcode: bWF3ZToxMjM0NQ==
        customer = new Customer("mawe","Martin","Weik","12345");
        customer.setPhoneNumber("+4369912345678");
        customer.setMail("martin@weik.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);
        customer.setAddress("blabla, 1234 Wien");
        customer.setCreditCard("342503083304998");
        customerRepository.save(customer);

        //authcode: ZnJhbms6bm9tb25leQ==
        customer = new Customer("frank","frank","nomoney","nomoney");
        customer.setPhoneNumber("+4368000000000");
        customer.setMail("a.b@c.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);
        customer.setAddress("No Name Street 123");
        customer.setCreditCard("6011671952963165");
        customerRepository.save(customer);
        
        //authcode: dGl3ZToxMjM0NTY=
        customer = new Customer("tiwe","Till","Weisser","123456");
        customer.setPhoneNumber("+43777611611372");
        customer.setMail("till.weisser@gmail.com");
        customer.setNotificationType(CustomerNotificationType.MAIL);
        customer.setAddress("lakierergasse 12");
        customer.setCreditCard("60134519567831652");
        customerRepository.save(customer);

        //authcode: YWdmcjoxMjM0NTY=
        customer = new Customer("agfr","Agnes","Froeschl","123456");
        customer.setPhoneNumber("+43634566789");
        customer.setMail("agnes.froeschl@gmail.com");
        customer.setNotificationType(CustomerNotificationType.MAIL);
        customer.setAddress("Breitengasse 12");
        customer.setCreditCard("6013451956783165");
        customerRepository.save(customer);
    }
}
