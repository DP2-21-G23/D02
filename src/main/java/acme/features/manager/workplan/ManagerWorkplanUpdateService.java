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
		model.setAttribute("modelTasks", taskIds.toString());
		
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
		
		if(!errors.hasErrors("modelTasks") && !request.getModel().getString("modelTasks").equals("")) {
			boolean hasNonExistingTasks = false; 
			boolean hasTasksOutsidePeriod = false;
			boolean hasTasksNowOwned = false;
			boolean hasPrivateTasks = false;
			final StringBuilder nonExistingTasks = new StringBuilder();
			final StringBuilder tasksOutsidePeriod = new StringBuilder();
			final StringBuilder tasksNotOwned = new StringBuilder();
			final StringBuilder privateTasks = new StringBuilder();
			final Set<String> tasks = new HashSet<String>();
			

			final String[] taskIds = ((String) request.getModel().getAttribute("modelTasks")).split(",");
			for (final String t : taskIds) {
				tasks.add(t.trim());
			}
			
			for(String taskId : tasks) {
				taskId = taskId.trim();
				if (!taskId.equals("")) {
					if (!this.repository.taskExists(taskId)) {
						if (!nonExistingTasks.toString().isEmpty()) {
							nonExistingTasks.append(", ");
						} else {
							hasNonExistingTasks = true;
						}
						nonExistingTasks.append(taskId);
						continue;
					}
					final Task t = this.repository.findOneTaskByTaskId(taskId);
					boolean taskOwned;
					final Manager manager;
					Principal principal;

					manager = t.getOwner();
					principal = request.getPrincipal();
					taskOwned = manager.getUserAccount().getId() == principal.getAccountId();
					if(!taskOwned) {
						if (!tasksNotOwned.toString().isEmpty()) {
							tasksNotOwned.append(", ");
						} else {
							hasTasksNowOwned = true;
						}
						tasksNotOwned.append(taskId);
						continue;
					}
					
					final boolean boundaries = !this.repository.taskIsInsideExecutionPeriod(taskId, entity.getExecutionPeriodStart(), entity.getExecutionPeriodEnd());
					if(boundaries) {
						if (!tasksOutsidePeriod.toString().isEmpty()) {
							tasksOutsidePeriod.append(", ");
						} else {
							hasTasksOutsidePeriod = true;
						}
						tasksOutsidePeriod.append(taskId);
					}
					
					if(!this.repository.taskIsPublic(taskId) && entity.getIsPublic()) {
						if (!privateTasks.toString().isEmpty()) {
							privateTasks.append(", ");
						} else {
							hasPrivateTasks = true;
						}
						privateTasks.append(taskId);
					}
				}
			}
			if (hasTasksNowOwned) {
				errors.state(request, false, "modelTasks", "manager.workplan.form.error.tasks.not-owned");
				errors.state(request, false, "modelTasks", " " + tasksNotOwned.toString()+"\n\r");
			}
			if (hasNonExistingTasks) {
				errors.state(request, false, "modelTasks", "manager.workplan.form.error.tasks.wrong-task-ids");
				errors.state(request, false, "modelTasks", " " + nonExistingTasks.toString()+"\n\r");
			}
			if (hasTasksOutsidePeriod) {
				errors.state(request, false, "modelTasks", "manager.workplan.form.error.tasks.wrong-task-dates");
				errors.state(request, false, "modelTasks", " " + tasksOutsidePeriod.toString()+"\n\r");
			}
			if (hasPrivateTasks) {
				errors.state(request, false, "isPublic", "manager.workplan.form.error.publish");
				errors.state(request, false, "isPublic", "manager.workplan.form.error.publish.tasks");
				errors.state(request, false, "isPublic", " " + privateTasks.toString());
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
		
		final Set<Task> tasks = new HashSet<Task>();
		final String[] taskIds = (request.getModel().getString("modelTasks")).split(",");
		for (final String t : taskIds) {
			tasks.add(this.repository.findOneTaskByTaskId(t.trim()));
		}
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
