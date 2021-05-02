package acme.features.manager.workplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.entities.workplans.Workplan;
import acme.features.manager.task.ManagerTaskRepository;
import acme.framework.components.Errors;
import acme.framework.components.HttpMethod;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.components.Response;
import acme.framework.entities.Principal;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractUpdateService;
import acme.utilities.SpamModule;
import acme.utilities.SpamModule.SpamModuleResult;
import acme.utilities.SpamRepository;

@Service
public class ManagerWorkplanUpdateService implements AbstractUpdateService<Manager, Workplan> {

	@Autowired
	protected ManagerWorkplanRepository repository;
	
	@Autowired
	protected ManagerTaskRepository taskRepository;
	
	@Autowired
	protected SpamRepository spamRepository;
	
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
		
		request.unbind(entity, model, "title", "executionPeriodStart", "executionPeriodEnd", "workload", "isPublic");
		
		model.setAttribute("nuevaTask", "");
		model.setAttribute("borrarTask", "");
		
		final SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		final Date earliestTask = this.repository.earliestTaskDateFromWorkplan(entity.getId());
		final Calendar aux = Calendar.getInstance();
		aux.setTime(earliestTask);
	    aux.set(Calendar.HOUR, 8);
	    aux.set(Calendar.MINUTE, 0);
		aux.add(Calendar.DAY_OF_MONTH, -1);
	    final Date earliestDate = aux.getTime();
	    final StringBuilder suggestionBuilder = new StringBuilder();
	    suggestionBuilder.append("<"+formato.format(earliestDate)+", ");
	    
	    final Date latestTask = this.repository.latestTaskDateFromWorkplan(entity.getId());
	    aux.setTime(latestTask);
	    aux.set(Calendar.HOUR_OF_DAY, 17);
	    aux.set(Calendar.MINUTE, 0);
		aux.add(Calendar.DAY_OF_MONTH, 1);
	    final Date latestDate = aux.getTime();
	    suggestionBuilder.append(formato.format(latestDate)+">");
	    
		model.setAttribute("suggestion", suggestionBuilder.toString());
		
	}

	@Override
	public Workplan findOne(final Request<Workplan> request) {
		assert request != null;

		final int id = request.getModel().getInteger("id");

		return this.repository.findOneWorkplanById(id);
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
		
		if(!errors.hasErrors("executionPeriodEnd")) {
			final Boolean incorrectDate = this.repository.isNotPossibleModificatePeriod(entity.getId(), entity.getExecutionPeriodStart(), entity.getExecutionPeriodEnd());
			errors.state(request, !incorrectDate, "executionPeriodEnd", "manager.task.form.error.incorrect-date");
		}
		
		if(!errors.hasErrors("isPublic") && entity.getIsPublic()==true) {
			final Boolean publish = this.repository.isNotPossibleMakePublic(entity.getId());
			errors.state(request, !publish, "isPublic", "manager.workplan.form.error.publish");
		}
		
		if(!errors.hasErrors("nuevaTask") && !request.getModel().getString("nuevaTask").equals("")) {
			
			final String taskId = request.getModel().getString("nuevaTask");
			final Task t = this.repository.searchTask(taskId);
			if(t!=null) {
				final Boolean boundaries = entity.getExecutionPeriodStart().after(t.getStartMoment())||entity.getExecutionPeriodEnd().before(t.getEndMoment());
				errors.state(request, !boundaries, "nuevaTask", "manager.workplan.form.error.nueva-task-fechas");
				
				boolean mine;
				final Manager manager;
				Principal principal;

				manager = t.getOwner();
				principal = request.getPrincipal();
				mine = manager.getUserAccount().getId() == principal.getAccountId();
				errors.state(request, mine, "nuevaTask", "manager.workplan.form.error.nueva-task-propia");
				
				final Boolean contains = entity.getTasks().contains(t);
				errors.state(request, !contains, "nuevaTask", "manager.workplan.form.error.nueva-task-contenida");
			}else {
				errors.state(request, false, "nuevaTask", "manager.workplan.form.error.nueva-task-no-existe");
			}
			
			
		}
		
		if(!errors.hasErrors("borrarTask") && !request.getModel().getString("borrarTask").equals("")) {
			final String taskId = request.getModel().getString("borrarTask");
			final Task t = this.repository.searchTask(taskId);
			if(t!=null) {
				final Boolean contains = entity.getTasks().contains(t);
				errors.state(request, contains, "borrarTask", "manager.workplan.form.error.borrar-task-no-contenida");
			}else {
				errors.state(request, false, "borrarTask", "manager.workplan.form.error.nueva-task-no-existe");
			}
			
		}

		if(entity.getIsPublic()) {
			final SpamModule sm = new SpamModule(this.spamRepository);
			
			final SpamModuleResult spamResult = sm.checkSpam(entity);
			if(spamResult.isHasErrors()) {
				errors.state(request, false, "isPublic", "manager.workplan.form.error.spam.has-errors");
			} else if (spamResult.isSpam()){
				errors.state(request, false, "isPublic", "manager.workplan.form.error.spam.is-spam");
			}
		}
		
	
		
	}

	@Override
	public void update(final Request<Workplan> request, final Workplan entity) {
		assert request != null;
		assert entity != null;
		
		final String nueva = request.getModel().getString("nuevaTask");
		final String borrar = request.getModel().getString("borrarTask");
		final Task tNueva = this.repository.searchTask(nueva);
		final Task tBorrar = this.repository.searchTask(borrar);
		
		final Collection<Task> tasks = entity.getTasks();
		tasks.add(tNueva);
		tasks.remove(tBorrar);
		entity.setTasks(tasks);
		
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
