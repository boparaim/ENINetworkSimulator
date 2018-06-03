package ca.empowered.nms.simulator.db.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.empowered.nms.simulator.db.model.Event;

/**
 * This is auto implemented by Spring
 * @author mboparai
 *
 */
@Transactional
public interface EventRepository extends CrudRepository<Event, Long> {

	@Query("select count(e.id) from Event e")
	Integer getTotalEventCount();
	
}
