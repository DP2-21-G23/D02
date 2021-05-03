/*
 * AuthenticatedTaskCreateService.java
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.framework.components.Errors;
import acme.framework.components.HttpMethod;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.components.Response;
import acme.framework.entities.Principal;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractCreateService;
import acme.utilities.SpamModule;
import acme.utilities.SpamModule.SpamModuleResult;
import acme.utilities.SpamRepository;

@Service
public class ManagerTaskCreateService implements AbstractCreateService<Manager, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected ManagerTaskRepository repository;
	
	@Autowired
	protected SpamRepository spamRepository;

	// AbstractCreateService<Manager, Task> interface ---------------


	@Override
	public boolean authorise(final Request<Task> request) {
		assert request != null;

		return true;
	}

	@Override
	public void bind(final Request<Task> request, final Task entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Task> request, final Task entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "taskId", "title", "startMoment", "endMoment", "workloadHours", "workloadFraction",
			"description", "link", "isPublic");
	}

	@Override
	public Task instantiate(final Request<Task> request) {
		assert request != null;

		final Task result;
		final Principal principal;
		final Manager manager;
		int userAccountId;
		
		result = new Task();
		principal = request.getPrincipal();
		
		userAccountId = principal.getActiveRoleId();
		manager = this.repository.findOneManagerById(userAccountId);
		
		result.setOwner(manager);
		
		return result;
	}

	@Override
	public void validate(final Request<Task> request, final Task entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		final Date now = new Date(System.currentTimeMillis());
		
		if (!errors.hasErrors("startMoment")) {
			final Boolean isAfter = entity.getStartMoment().after(now);
			errors.state(request, isAfter, "startMoment", "manager.task.form.error.past-startMoment");
		}
		if (!errors.hasErrors("endMoment")) {
			final Boolean isAfter = entity.getEndMoment().after(now);
			errors.state(request, isAfter, "endMoment", "manager.task.form.error.past-endMoment");
		}
		if(!errors.hasErrors("endMoment") && !errors.hasErrors("startMoment")) {
			final Boolean isAfter = entity.getEndMoment().after(entity.getStartMoment());
			errors.state(request, isAfter, "endMoment", "manager.task.form.error.incorrect-interval");
			
            final Calendar moments = new GregorianCalendar();
            moments.setTime(entity.getStartMoment());
            final LocalDateTime start = LocalDateTime.ofInstant(moments.toInstant(), ZoneId.systemDefault());
            moments.setTime(entity.getEndMoment());
            final LocalDateTime end = LocalDateTime.ofInstant(moments.toInstant(), ZoneId.systemDefault());
            
            final Long datediff = ChronoUnit.MINUTES.between(start, end);
            Long workload = (long) entity.getWorkloadHours()*60;
            if(entity.getWorkloadFraction()!=null) {
                workload+=entity.getWorkloadFraction();
            }
            
            if(datediff.compareTo(workload)<0) {
                errors.state(request, false, "workloadHours", "manager.task.form.error.incorrect-workload");
            }
		}
		
		final SpamModule sm = new SpamModule(this.spamRepository);
		
		final SpamModuleResult spamResult = sm.checkSpam(entity);
		if(spamResult.isHasErrors()) {
			errors.state(request, false, "isPublic", "manager.task.form.error.spam.has-errors");
		} else if (spamResult.isSpam()){
			errors.state(request, false, "isPublic", "manager.task.form.error.spam.is-spam");
		}
		
		if (!errors.hasErrors("taskId")) {
			errors.state(request, !this.repository.checkUniqueTicker(entity.getTaskId()), "taskId", "manager.task.form.error.spam.unique-task-id");
		}
		
		if (!errors.hasErrors("workloadHours")) {
			final Boolean positiveWorkloadHours = entity.getWorkloadHours() >= 0;
			errors.state(request, positiveWorkloadHours, "workloadHours", "manager.task.form.error.negative-workload");
		}
		
		if(!errors.hasErrors("workloadHours") && entity.getWorkloadHours()==0 && (entity.getWorkloadFraction()==null || entity.getWorkloadFraction()<=0)) {
			errors.state(request, false, "workloadFraction", "manager.task.form.error.incorrect-workloadFraction");
		}
	}
		

	@Override
	public void create(final Request<Task> request, final Task entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
	}

	@Override
	public void onSuccess(final Request<Task> request, final Response<Task> response) {
		assert request != null;
		assert response != null;

		if (request.isMethod(HttpMethod.POST)) {
			PrincipalHelper.handleUpdate();
		}
	}

}
