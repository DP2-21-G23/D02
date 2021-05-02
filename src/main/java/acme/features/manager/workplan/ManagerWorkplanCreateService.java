package acme.features.manager.workplan;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.workplans.Workplan;
import acme.framework.components.Errors;
import acme.framework.components.HttpMethod;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.components.Response;
import acme.framework.entities.Principal;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractCreateService;
import acme.utilities.SpamRepository;

@Service
public class ManagerWorkplanCreateService implements AbstractCreateService<Manager, Workplan> {

	@Autowired
	protected ManagerWorkplanRepository repository;
	
	@Autowired
	protected SpamRepository spamRepository;
	
	@Override
	public boolean authorise(final Request<Workplan> request) {
		assert request != null;
		
		return true;
	}

	@Override
	public void bind(final Request<Workplan> request, final Workplan entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
		
	}

	@Override
	public void unbind(final Request<Workplan> request, final Workplan entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		request.unbind(entity, model, "title", "executionPeriodStart", "executionPeriodEnd");
	
		
	}

	@Override
	public Workplan instantiate(final Request<Workplan> request) {
		assert request != null;

		final Workplan result;
		final Principal principal;
		final Manager manager;
		int userAccountId;
		
		result = new Workplan();
		principal = request.getPrincipal();
		
		userAccountId = principal.getActiveRoleId();
		manager = this.repository.findOneManagerById(userAccountId);
		
		result.setOwner(manager);
		
		result.setIsPublic(false);
		
		return result;
	}

	@Override
	public void validate(final Request<Workplan> request, final Workplan entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		final Date now = new Date(System.currentTimeMillis());
		
		if (!errors.hasErrors("executionPeriodStart")) {
			final Boolean isAfter = entity.getExecutionPeriodStart().after(now);
			errors.state(request, isAfter, "executionPeriodStart", "manager.workplan.form.error.past-executionPeriodStart");
		}
		if (!errors.hasErrors("executionPeriodEnd")) {
			final Boolean isAfter = entity.getExecutionPeriodEnd().after(now);
			errors.state(request, isAfter, "executionPeriodEnd", "manager.workplan.form.error.past-executionPeriodEnd");
		}
		if(!errors.hasErrors("executionPeriodEnd") && !errors.hasErrors("executionPeriodStart")) {
			final Boolean isAfter = entity.getExecutionPeriodEnd().after(entity.getExecutionPeriodStart());
			errors.state(request, isAfter, "executionPeriodEnd", "manager.workplan.form.error.incorrect-interval");
		}

	}

	@Override
	public void create(final Request<Workplan> request, final Workplan entity) {
		assert request != null;
		assert entity != null;
		
		this.repository.save(entity);
		
	}
	
	@Override
	public void onSuccess(final Request<Workplan> request, final Response<Workplan> response) {
		assert request != null;
		assert response != null;

		if (request.isMethod(HttpMethod.POST)) {
			PrincipalHelper.handleUpdate();
		}
	}

}
