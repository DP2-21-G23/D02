package acme.features.anonymous.workplan;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.workplans.Workplan;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Anonymous;
import acme.framework.services.AbstractListService;

@Service
public class AnonymousWorkplanListService implements AbstractListService<Anonymous, Workplan> {

	@Autowired
	protected AnonymousWorkplanRepository repository;
	
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

		request.unbind(entity, model, "title", "executionPeriodStart", "executionPeriodEnd", "workload");
		
	}


	@Override
	public Collection<Workplan> findMany(final Request<Workplan> request) {
		assert request != null;

		Collection<Workplan> result;
		result = this.repository.findMany();

		return result;
	}

}
