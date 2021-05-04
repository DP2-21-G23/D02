package acme.features.manager.workplan;

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
public interface ManagerWorkplanRepository extends AbstractRepository {
	
	@Query("select wp from Workplan wp where wp.owner.id = :id")
	Collection<Workplan> findWorkplansByManagerId(@Param("id") int id);

	@Query("select wp from Workplan wp where wp.id = :id")
	Workplan findOneWorkplanById(@Param("id") int id);
	
	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(@Param("id") int id);

	@Query("select count(wp)>0 from Workplan wp join wp.tasks as t where wp.id = :id and (:executionPeriodStart > t.startMoment or :executionPeriodEnd < t.endMoment)")
	Boolean isNotPossibleModificatePeriod(@Param("id") int id, @Param("executionPeriodStart") Date executionPeriodStart, @Param("executionPeriodEnd") Date executionPeriodEnd);

	@Query("select count(wp)>0 from Workplan wp join wp.tasks as t where wp.id = :id and t.isPublic = false")
	Boolean isNotPossibleMakePublic(@Param("id") int id);

	@Query("select t from Task t where t.taskId = ?1")
	Task searchTask(String taskId);
	
	@Query("select min(t.startMoment) from Workplan wp join wp.tasks as t where wp.id = ?1")
	Date earliestTaskDateFromWorkplan(int workplanId);

	@Query("select max(t.endMoment) from Workplan wp join wp.tasks as t where wp.id = ?1")
	Date latestTaskDateFromWorkplan(int id);
	
	@Query("select count(t)>0 from Task t where t.taskId = ?1")
	boolean taskExists(String taskId);
	
	@Query("select case when (:executionPeriodStart <= t.startMoment and :executionPeriodEnd >= t.endMoment) then true else false end from Task t where t.taskId = :taskId")
	boolean taskIsInsideExecutionPeriod(@Param("taskId") String taskId, @Param("executionPeriodStart") Date executionPeriodStart, @Param("executionPeriodEnd") Date executionPeriodEnd);
	
	@Query("select t from Task t where t.taskId = ?1")
	Task findOneTaskByTaskId(String taskId);

	@Query("select t.isPublic from Task t where t.taskId = :taskId")
	boolean taskIsPublic(@Param("taskId") String taskId);
	
	@Query("select t.taskId from Task t where :executionPeriodEnd >= t.endMoment and :executionPeriodStart <= t.startMoment and t.owner.id = :ownerId")
	Collection<String> findValidTasks(@Param("executionPeriodStart") Date executionPeriodStart, @Param("executionPeriodEnd") Date executionPeriodEnd, @Param("ownerId") int ownerId);
	
	@Query("select t.taskId from Task t where :executionPeriodEnd >= t.endMoment and :executionPeriodStart <= t.startMoment and t.owner.id = :ownerId and t.isPublic = true")
	Collection<String> findValidTasksPublicWorkPlan(@Param("executionPeriodStart") Date executionPeriodStart, @Param("executionPeriodEnd") Date executionPeriodEnd, @Param("ownerId") int ownerId);
	
	@Query("select case when (t.isPublic = false) then true else false end from Task t where t.taskId = ?1")
	Boolean taskIsPrivateByTaskId(String taskId);
}
