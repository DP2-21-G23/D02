/*
 * AuthenticatedConsumerRepository.java
 *
 * Copyright (C) 2012-2021 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.manager.task;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.entities.workplans.Workplan;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface ManagerTaskRepository extends AbstractRepository {

	@Query("select ct from Task ct where ct.owner.id = :id")
	Collection<Task> findManagerTaskById(@Param("id") int id);

	@Query("select t from Task t where t.id = :id")
	Task findOneTaskById(@Param("id") int id);
	
	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(@Param("id") int id);
	
	@Query("select count(wp)>0 from Workplan wp join wp.tasks as t where t.id = :id and (:startMoment < wp.executionPeriodStart or :endMoment > wp.executionPeriodEnd)")
	Boolean isNotPossibleModificateMoment(@Param("id") int id, @Param("startMoment") Date startMoment, @Param("endMoment") Date endMoment);
	
	@Query("select count(wp)>0 from Workplan wp join wp.tasks as t where t.id = :id and :esPublico = false and wp.isPublic = true")
	Boolean isNotPossibleMakePublic(@Param("id") int id, @Param("esPublico") boolean esPublico);

	@Query("select wp from Workplan wp join wp.tasks as t where t.id = :id")
	Collection<Workplan> findWorkplansByTask(@Param("id") int id);
	
	@Query("select w.tasks from Workplan w where w.id = ?1")
	Collection<Task> findManyByWorkplanId(int workplanId);

	@Query("select count(t)>0 from Task t where t.taskId = ?1")
	Boolean checkUniqueTicker(String taskId);
	
}
