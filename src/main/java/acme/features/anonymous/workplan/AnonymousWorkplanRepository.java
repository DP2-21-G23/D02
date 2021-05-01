package acme.features.anonymous.workplan;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.entities.workplans.Workplan;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface AnonymousWorkplanRepository extends AbstractRepository {

	@Query("select wp from Workplan wp where wp.executionPeriodEnd > current_timestamp and wp.isPublic = true")
	Collection<Workplan> findMany();

	@Query("select wp from Workplan wp where wp.id = :id")
	Workplan findOneWorkplanById(@Param("id") int id);
}
