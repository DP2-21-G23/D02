
package acme.features.administrator.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.forms.Dashboard;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Administrator;
import acme.framework.services.AbstractShowService;

@Service
public class AdministratorDashboardShowService implements AbstractShowService<Administrator, Dashboard> {

	@Autowired
	private AdministratorDashboardRepository		repository;

	@Override
	public boolean authorise(final Request<Dashboard> request) {
		assert request != null;

		return true;
	}

	@Override
	public void unbind(final Request<Dashboard> request, final Dashboard entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		final int numberOfPrivateTasks = this.repository.numberOfPrivateTasks();
		final int numberOfPublicTasks = this.repository.numberOfPublicTasks();
		final int numberOfFinishedTasks = this.repository.numberOfFinishedTasks();
		final int numberOfUnfinishedTasks = this.repository.numberOfUnfinishedTasks();

		final List<Object[]> taskPeriodsStats = new ArrayList<Object[]>(this.repository.taskPeriodsStats());
		final int minimumTaskPeriods = (int) taskPeriodsStats.get(0)[0];
		final String minTaskPeriodsDays = minimumTaskPeriods + "";
		model.setAttribute("minTaskPeriodsDays", minTaskPeriodsDays);
		final int maximumTaskPeriods = (int) taskPeriodsStats.get(0)[1];
		final String maxTaskPeriodsDays = maximumTaskPeriods + "";
		model.setAttribute("maxTaskPeriodsDays", maxTaskPeriodsDays);
		final double avgTaskPeriods = (double) taskPeriodsStats.get(0)[2];
		final String avgTaskPeriodsDays = String.format("%.0f", avgTaskPeriods);
		model.setAttribute("avgTaskPeriodsDays", avgTaskPeriodsDays);
		final double stddevTaskPeriods = (double) taskPeriodsStats.get(0)[3];
		final String stddevTaskPeriodsDays = String.format("%.0f", stddevTaskPeriods);
		model.setAttribute("stddevTaskPeriodsDays", stddevTaskPeriodsDays);

		final List<Object[]> taskWorkloadsStats = new ArrayList<Object[]>(this.repository.taskWorkloadsStats());
		final int minimumTaskWorkloads = (int) taskWorkloadsStats.get(0)[0];
		final String minTaskWorkloadsHoursMinutes = String.format("%d h %d m", minimumTaskWorkloads/60, minimumTaskWorkloads%60);
		model.setAttribute("minTaskWorkloadsHoursMinutes", minTaskWorkloadsHoursMinutes);
		final int maximumTaskWorkloads = (int) taskWorkloadsStats.get(0)[1];
		final String maxTaskWorkloadsHoursMinutes = String.format("%d h %d m", maximumTaskWorkloads/60, maximumTaskWorkloads%60);
		model.setAttribute("maxTaskWorkloadsHoursMinutes", maxTaskWorkloadsHoursMinutes);
		final double avgTaskWorkloads = (double) taskWorkloadsStats.get(0)[2];
		final String avgTaskWorkloadsHoursMinutes = String.format("%.0f h %.0f m", avgTaskWorkloads/60, avgTaskWorkloads%60);
		model.setAttribute("avgTaskWorkloadsHoursMinutes", avgTaskWorkloadsHoursMinutes);
		final double stddevTaskWorkloads = (double) taskWorkloadsStats.get(0)[3];
		final String stddevTaskWorkloadsHoursMinutes = String.format("%.0f h %.0f m", stddevTaskWorkloads/60, stddevTaskWorkloads%60);
		model.setAttribute("stddevTaskWorkloadsHoursMinutes", stddevTaskWorkloadsHoursMinutes);
		
		final int numberOfPrivateWorkPlans = this.repository.numberOfPrivateWorkPlans();
		final int numberOfPublicWorkPlans = this.repository.numberOfPublicWorkPlans();
		final int numberOfFinishedWorkPlans = this.repository.numberOfFinishedWorkPlans();
		final int numberOfUnfinishedWorkPlans = this.repository.numberOfUnfinishedWorkPlans();
		
		final List<Object[]> workPlanPeriodsStats = new ArrayList<Object[]>(this.repository.workPlanPeriodsStats());
		final int minimumWorkPlanPeriods = (int) workPlanPeriodsStats.get(0)[0];
		final String minWorkPlanPeriodsDays = minimumWorkPlanPeriods + "";
		model.setAttribute("minWorkPlanPeriodsDays", minWorkPlanPeriodsDays);
		final int maximumWorkPlanPeriods = (int) workPlanPeriodsStats.get(0)[1];
		final String maxWorkPlanPeriodsDays = maximumWorkPlanPeriods + "";
		model.setAttribute("maxWorkPlanPeriodsDays", maxWorkPlanPeriodsDays);
		final double avgWorkPlanPeriods = (double) workPlanPeriodsStats.get(0)[2];
		final String avgWorkPlanPeriodsDays = String.format("%.0f", avgWorkPlanPeriods);
		model.setAttribute("avgWorkPlanPeriodsDays", avgWorkPlanPeriodsDays);
		final double stddevWorkPlanPeriods = (double) workPlanPeriodsStats.get(0)[3];
		final String stddevWorkPlanPeriodsDays = String.format("%.0f", stddevWorkPlanPeriods);
		model.setAttribute("stddevWorkPlanPeriodsDays", stddevWorkPlanPeriodsDays);

		final List<Object[]> workPlanWorkloadsStats = new ArrayList<Object[]>(this.repository.workPlanWorkloadsStats());
		final int minimumWorkPlanWorkloads = (int) workPlanWorkloadsStats.get(0)[0];
		final String minWorkPlanWorkloadsHoursMinutes = String.format("%d h %d m", minimumWorkPlanWorkloads/60, minimumWorkPlanWorkloads%60);
		model.setAttribute("minWorkPlanWorkloadsHoursMinutes", minWorkPlanWorkloadsHoursMinutes);
		final int maximumWorkPlanWorkloads = (int) workPlanWorkloadsStats.get(0)[1];
		final String maxWorkPlanWorkloadsHoursMinutes = String.format("%d h %d m", maximumWorkPlanWorkloads/60, maximumWorkPlanWorkloads%60);
		model.setAttribute("maxWorkPlanWorkloadsHoursMinutes", maxWorkPlanWorkloadsHoursMinutes);
		final double avgWorkPlanWorkloads = (double) workPlanWorkloadsStats.get(0)[2];
		final String avgWorkPlanWorkloadsHoursMinutes = String.format("%.0f h %.0f m", avgWorkPlanWorkloads/60, avgWorkPlanWorkloads%60);
		model.setAttribute("avgWorkPlanWorkloadsHoursMinutes", avgWorkPlanWorkloadsHoursMinutes);
		final double stddevWorkPlanWorkloads = (double) workPlanWorkloadsStats.get(0)[3];
		final String stddevWorkPlanWorkloadsHoursMinutes = String.format("%.0f h %.0f m", stddevWorkPlanWorkloads/60, stddevWorkPlanWorkloads%60);
		model.setAttribute("stddevWorkPlanWorkloadsHoursMinutes", stddevWorkPlanWorkloadsHoursMinutes);
		
		final int numberOfWorkPlans = this.repository.numberOfWorkPlans();
		final int numberOfPublishedWorkPlans = this.repository.numberOfPublishedWorkPlans();
		final int numberOfUnpublishedWorkPlans = this.repository.numberOfUnpublishedWorkPlans();
		
		entity.setNumberOfPrivateTasks(numberOfPrivateTasks);
		entity.setNumberOfPublicTasks(numberOfPublicTasks);
		entity.setNumberOfFinishedTasks(numberOfFinishedTasks);
		entity.setNumberOfUnfinishedTasks(numberOfUnfinishedTasks);
		
		entity.setMinimumTaskPeriods(minimumTaskPeriods);
		entity.setMaximumTaskPeriods(maximumTaskPeriods);
		entity.setAvgTaskPeriods(avgTaskPeriods);
		entity.setStddevTaskPeriods(stddevTaskPeriods);
		
		entity.setMinimumTaskWorkloads(minimumTaskWorkloads);
		entity.setMaximumTaskWorkloads(maximumTaskWorkloads);
		entity.setAvgTaskWorkloads(avgTaskWorkloads);
		entity.setStddevTaskWorkloads(stddevTaskWorkloads);
		
		entity.setNumberOfPrivateWorkPlans(numberOfPrivateWorkPlans);
		entity.setNumberOfPublicWorkPlans(numberOfPublicWorkPlans);
		entity.setNumberOfFinishedWorkPlans(numberOfFinishedWorkPlans);
		entity.setNumberOfUnfinishedWorkPlans(numberOfUnfinishedWorkPlans);
		
		entity.setMinimumWorkPlanPeriods(minimumWorkPlanPeriods);
		entity.setMaximumWorkPlanPeriods(maximumWorkPlanPeriods);
		entity.setAvgWorkPlanPeriods(avgWorkPlanPeriods);
		entity.setStddevWorkPlanPeriods(stddevWorkPlanPeriods);
		
		entity.setMinimumWorkPlanWorkloads(minimumWorkPlanWorkloads);
		entity.setMaximumWorkPlanWorkloads(maximumWorkPlanWorkloads);
		entity.setAvgWorkPlanWorkloads(avgWorkPlanWorkloads);
		entity.setStddevWorkPlanWorkloads(stddevWorkPlanWorkloads);
		
		entity.setNumberOfWorkPlans(numberOfWorkPlans);
		entity.setNumberOfPublishedWorkPlans(numberOfPublishedWorkPlans);
		entity.setNumberOfUnpublishedWorkPlans(numberOfUnpublishedWorkPlans);
		
		request.unbind(entity, model, "numberOfPrivateTasks", "numberOfPublicTasks", "numberOfFinishedTasks", "numberOfUnfinishedTasks",
			"minimumTaskPeriods", "maximumTaskPeriods", "avgTaskPeriods", "stddevTaskPeriods",
			"minimumTaskWorkloads", "maximumTaskWorkloads", "avgTaskWorkloads", "stddevTaskWorkloads",
			"numberOfPrivateWorkPlans", "numberOfPublicWorkPlans", "numberOfFinishedWorkPlans", "numberOfUnfinishedWorkPlans",
			"minimumWorkPlanPeriods", "maximumWorkPlanPeriods", "avgWorkPlanPeriods", "stddevWorkPlanPeriods",
			"minimumWorkPlanWorkloads", "maximumWorkPlanWorkloads", "avgWorkPlanWorkloads", "stddevWorkPlanWorkloads",
			"numberOfWorkPlans", "numberOfPublishedWorkPlans", "numberOfUnpublishedWorkPlans");
	}

	@Override
	public Dashboard findOne(final Request<Dashboard> request) {
		return new Dashboard();
	}

}
