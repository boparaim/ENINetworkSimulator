package ca.empowered.nms.simulator.db.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.empowered.nms.simulator.db.model.Edge;

/**
 * This is auto implemented by Spring
 * @author mboparai
 *
 */
@Transactional
public interface EdgeRepository extends CrudRepository<Edge, Long> {

	@Query("select count(e.id) from Edge e")
	Integer getTotalEdgeCount();

}
