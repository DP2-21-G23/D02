
package acme.forms;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dashboard implements Serializable {

	// Serialisation identifier
	private static final long	serialVersionUID	= 1L;

	// Attributes

	int						numberOfPrivateTasks;
	int						numberOfPublicTasks;
	
	int						numberOfFinishedTasks;
	int						numberOfUnfinishedTasks;

	int						minimumTaskPeriods;
	int						maximumTaskPeriods;
	double						avgTaskPeriods;
	double						stddevTaskPeriods;

	int						minimumTaskWorkloads;
	int						maximumTaskWorkloads;
	double						avgTaskWorkloads;
	double						stddevTaskWorkloads;

	int						numberOfPrivateWorkPlans;
	int						numberOfPublicWorkPlans;
	
	int						numberOfFinishedWorkPlans;
	int						numberOfUnfinishedWorkPlans;

	int						minimumWorkPlanPeriods;
	int						maximumWorkPlanPeriods;
	double						avgWorkPlanPeriods;
	double						stddevWorkPlanPeriods;

	int						minimumWorkPlanWorkloads;
	int						maximumWorkPlanWorkloads;
	double						avgWorkPlanWorkloads;
	double						stddevWorkPlanWorkloads;
	
	int						numberOfWorkPlans;
	int						numberOfPublishedWorkPlans;
	int						numberOfUnpublishedWorkPlans;
	
}
