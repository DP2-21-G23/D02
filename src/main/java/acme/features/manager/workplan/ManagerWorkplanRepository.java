package acme.features.manager.workplan;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.entities.roles.Manager;
import acme.entities.workplans.Workplan;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface ManagerWorkplanRepository extends AbstractRepository {
	
	@Query("select wp from Workplan wp where wp.owner.id = :id")
	Collection<Workplan> findWorkplansByManagerId(@Param("id") int id);

	@Query("select wp from Workplan wp where wp.id = :id")
	Workplan findOneWorkplanById(@Param("id") int id);
	
	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(@Param("id") int id);

}
