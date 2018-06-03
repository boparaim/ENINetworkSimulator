package ca.empowered.nms.simulator.db.dao;

import java.math.BigInteger;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ca.empowered.nms.simulator.db.model.Node;

/**
 * This is auto implemented by Spring
 * @author mboparai
 *
 */
@Transactional
public interface NodeRepository extends CrudRepository<Node, Long> {

	//@Query("select n.id from Node n where n.ip = :ip limit 1")
	@Query(value="select n.id from Node n where n.ip = :ip limit 1", nativeQuery=true)
	BigInteger findNodeIdForIp(@Param("ip") String ip);
	
	@Query("select count(n.id) from Node n")
	Integer getTotalNodeCount();

}
