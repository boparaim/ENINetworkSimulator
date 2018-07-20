package ca.empowered.nms.simulator.db.dao;

import java.math.BigInteger;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ca.empowered.nms.simulator.db.model.Edge;

/**
 * This is auto implemented by Spring
 * @author mboparai
 *
 */
@Transactional
public interface EdgeRepository extends CrudRepository<Edge, BigInteger> {

	@Query("select count(e.id) from Edge e")
	Integer getTotalEdgeCount();
	
	@Query(value="select e.id from Edge e where "
			+ "e.nodeIdA = :nodeIdA and "
			+ "e.ifIndexA = :ifIndexA and "
			+ "e.nodeIdB = :nodeIdB and "
			+ "e.ifIndexB = :ifIndexB limit 1", nativeQuery=true)
	BigInteger findEdgeId(@Param("nodeIdA") BigInteger nodeIdA, @Param("ifIndexA") String ifIndexA, 
							@Param("nodeIdB") BigInteger nodeIdB, @Param("ifIndexB") String ifIndexB);

}
