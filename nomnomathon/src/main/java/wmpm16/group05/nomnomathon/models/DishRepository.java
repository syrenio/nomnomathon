package wmpm16.group05.nomnomathon.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DishRepository extends CrudRepository<Dish,Long> {

}
