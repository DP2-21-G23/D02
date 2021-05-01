/*
 * AnonymousShoutListService.java
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractListService;

@Service
public class ManagerTaskListService implements AbstractListService<Manager, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected ManagerTaskRepository repository;

	// AbstractListService<Anonymous, Shout> interface --------------


	@Override
	public boolean authorise(final Request<Task> request) {
		assert request != null;

		return true;
	}

	@Override
	public void unbind(final Request<Task> request, final Task entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "title", "startMoment", "endMoment", "workloadHours");
	}

	@Override
	public Collection<Task> findMany(final Request<Task> request) {
		assert request != null;

		final Principal principal;
		int userId;
		
		principal = request.getPrincipal();
		userId = principal.getActiveRoleId();
		
		Collection<Task> result;
		
		if(request.getModel().hasAttribute("workplanId")) {
			result = this.repository.findManyByWorkplanId(request.getModel().getInteger("workplanId"));
		}else {
			result = this.repository.findManagerTaskById(userId);
		}
		

		return result;
	}

}
