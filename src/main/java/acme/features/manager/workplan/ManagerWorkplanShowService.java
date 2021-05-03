package acme.features.manager.workplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.entities.workplans.Workplan;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractShowService;

@Service
public class ManagerWorkplanShowService implements AbstractShowService<Manager, Workplan> {

	@Autowired
	protected ManagerWorkplanRepository repository;
	
	@Override
	public boolean authorise(final Request<Workplan> request) {
		assert request != null;
		
		boolean res;
		int workplanId;
		final Workplan workplan;
		final Manager manager;
		Principal principal;

		workplanId = request.getModel().getInteger("id");
		workplan = this.repository.findOneWorkplanById(workplanId);
		manager = workplan.getOwner();
		principal = request.getPrincipal();
		res = manager.getUserAccount().getId() == principal.getAccountId();
		
		return res;
	}

	@Override
	public void unbind(final Request<Workplan> request, final Workplan entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "title", "executionPeriodStart", "executionPeriodEnd", "workload", "isPublic");
		
		final SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		final Date earliestTask = this.repository.earliestTaskDateFromWorkplan(entity.getId());
		final Date latestTask = this.repository.latestTaskDateFromWorkplan(entity.getId());
		if(earliestTask != null && latestTask != null) {
			final Calendar aux = Calendar.getInstance();
			aux.setTime(earliestTask);
		    aux.set(Calendar.HOUR_OF_DAY, 8);
		    aux.set(Calendar.MINUTE, 0);
			aux.add(Calendar.DAY_OF_MONTH, -1);
		    final Date earliestDate = aux.getTime();
		    final StringBuilder suggestionBuilder = new StringBuilder();
		    suggestionBuilder.append("<"+formato.format(earliestDate)+", ");
		    
		    
		    aux.setTime(latestTask);
		    aux.set(Calendar.HOUR_OF_DAY, 17);
		    aux.set(Calendar.MINUTE, 0);
			aux.add(Calendar.DAY_OF_MONTH, 1);
		    final Date latestDate = aux.getTime();
		    suggestionBuilder.append(formato.format(latestDate)+">");
		    model.setAttribute("suggestion", suggestionBuilder.toString());
		} else {
			model.setAttribute("suggestion", "");
		}

		final Set<Task> tasks = new HashSet<Task>(entity.getTasks());
		final StringBuilder taskIds = new StringBuilder();
		
		for (final Task t : tasks) {
			if (!taskIds.toString().isEmpty()) {
				taskIds.append(", ");
			}
			taskIds.append(t.getTaskId());
		}
		model.setAttribute("modelTasks", taskIds);
		
		final StringBuilder validTaskIds = new StringBuilder();
		Collection<String> taskIdsCollection;
		if(entity.getIsPublic()) {
			taskIdsCollection = this.repository.findValidTasksPublicWorkPlan(entity.getExecutionPeriodStart(), entity.getExecutionPeriodEnd(), entity.getOwner().getId());
		} else {
			taskIdsCollection = this.repository.findValidTasks(entity.getExecutionPeriodStart(), entity.getExecutionPeriodEnd(), entity.getOwner().getId());
		}
		for (final String tId : taskIdsCollection) {
			if(!validTaskIds.toString().isEmpty()) {
				validTaskIds.append(", ");	
			}
			validTaskIds.append(tId);
		}
		model.setAttribute("validTaskIds", validTaskIds.toString());
		
	}

	@Override
	public Workplan findOne(final Request<Workplan> request) {
		assert request != null;

		Workplan result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneWorkplanById(id);

		return result;
	}

}
