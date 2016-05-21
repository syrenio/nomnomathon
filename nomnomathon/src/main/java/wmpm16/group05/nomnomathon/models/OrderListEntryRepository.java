package wmpm16.group05.nomnomathon.models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderListEntryRepository extends CrudRepository<OrderListEntry,Long> {

	List<OrderListEntry> findAllById(Long id);
	
}