package acme.features.manager.workplan;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.workplans.Workplan;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractListService;

@Service
public class ManagerWorkplanListService implements AbstractListService<Manager, Workplan> {

	@Autowired
	protected ManagerWorkplanRepository repository;
	
	@Override
	public boolean authorise(final Request<Workplan> request) {
		assert request != null;
		
		return true;
	}

	@Override
	public void unbind(final Request<Workplan> request, final Workplan entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		request.unbind(entity, model, "title", "isPublic", "executionPeriodStart", "executionPeriodEnd", "workload");
		
	}

	@Override
	public Collection<Workplan> findMany(final Request<Workplan> request) {
		assert request != null;

		final Principal principal;
		int userId;
		
		principal = request.getPrincipal();
		userId = principal.getActiveRoleId();
		
		Collection<Workplan> result;
		
		result = this.repository.findWorkplansByManagerId(userId);

		return result;
	}

}
