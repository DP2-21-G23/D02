<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<style>
div.container{
display: inline-block;
width: 100%;
text-align: center
}
</style>

<h1>
	<acme:message code="administrator.dashboard.form.title" />
</h1>

<br />
<br />
<acme:form>

	<acme:form-integer code="administrator.dashboard.form.tasks.private" path="numberOfPrivateTasks"/>
	<acme:form-integer code="administrator.dashboard.form.tasks.public" path="numberOfPublicTasks"/>
	<br />
	<br />

	<acme:form-integer code="administrator.dashboard.form.tasks.finished" path="numberOfFinishedTasks"/>
	<acme:form-integer code="administrator.dashboard.form.tasks.unfinished" path="numberOfUnfinishedTasks"/>
	<br />
	<br />
	
	<h4>
		<acme:message code="administrator.dashboard.form.stats.title.task-periods"/>
	</h4>
	<acme:form-textbox code="administrator.dashboard.form.stats.min.period" path="minTaskPeriodsDays"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.max.period" path="maxTaskPeriodsDays"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.avg.period" path="avgTaskPeriodsDays"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.stddev.period" path="stddevTaskPeriodsDays"/>

	<br />
	<br />
	
	<h4>	
		<acme:message code="administrator.dashboard.form.stats.title.task-workload"/>
	</h4>
	<acme:form-textbox code="administrator.dashboard.form.stats.min" path="minTaskWorkloadsHoursMinutes"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.max" path="maxTaskWorkloadsHoursMinutes"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.avg" path="avgTaskWorkloadsHoursMinutes"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.stddev" path="stddevTaskWorkloadsHoursMinutes"/>

	<br />
	<br />
	
	<acme:form-integer code="administrator.dashboard.form.work-plans.private" path="numberOfPrivateWorkPlans"/>
	<acme:form-integer code="administrator.dashboard.form.work-plans.public" path="numberOfPublicWorkPlans"/>
	<br />
	<br />

	<acme:form-integer code="administrator.dashboard.form.work-plans.finished" path="numberOfFinishedWorkPlans"/>
	<acme:form-integer code="administrator.dashboard.form.work-plans.unfinished" path="numberOfUnfinishedWorkPlans"/>

	<br />
	<br />
	
	<h4>	
		<acme:message code="administrator.dashboard.form.stats.title.work-plan-periods"/>
	</h4>
	<acme:form-textbox code="administrator.dashboard.form.stats.min.period" path="minWorkPlanPeriodsDays"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.max.period" path="maxWorkPlanPeriodsDays"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.avg.period" path="avgWorkPlanPeriodsDays"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.stddev.period" path="stddevWorkPlanPeriodsDays"/>

	<br />
	<br />
	
	<h4>
		<acme:message code="administrator.dashboard.form.stats.title.work-plan-workload"/>
	</h4>
	<acme:form-textbox code="administrator.dashboard.form.stats.min" path="minWorkPlanWorkloadsHoursMinutes"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.max" path="maxWorkPlanWorkloadsHoursMinutes"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.avg" path="avgWorkPlanWorkloadsHoursMinutes"/>
	<acme:form-textbox code="administrator.dashboard.form.stats.stddev" path="stddevWorkPlanWorkloadsHoursMinutes"/>
	
	<br />
	<br />
	<br />

</acme:form>
<br />
<br />

<h2></h2>
<div class="container">
	<h2><strong><acme:message code="administrator.dashboard.form.chart.title.work-plans"/></strong></h2>
	<br /><br />
	<canvas id="chart-area"></canvas>
</div>

<acme:message var="totalWorkPlansLabel" code="administrator.dashboard.form.chart.label.work-plans.total"/>
<acme:message var="publishedWorkPlansLabel" code="administrator.dashboard.form.chart.label.work-plans.published"/>
<acme:message var="unpublishedWorkPlansLabel" code="administrator.dashboard.form.chart.label.work-plans.unpublished"/>

<script type="text/javascript">
	$(document).ready(function() {
		var data = {
			labels : [
				"${totalWorkPlansLabel}", "${publishedWorkPlansLabel}", "${unpublishedWorkPlansLabel}"
			],
			datasets: [
				{
					data: [
						<jstl:out value="${numberOfWorkPlans}"/>,
						<jstl:out value="${numberOfPublishedWorkPlans}"/>,
						<jstl:out value="${numberOfUnpublishedWorkPlans}"/>
					],
					backgroundColor : "blue",
				}
			]
		};
		var options = {
				legend:{display:false},
				responsive : true,
				scales: {yAxes: [{
					ticks: {
						suggestedMin: 0.0
					}}]
				}
			};
		var canvas, context;
		
		canvas = document.getElementById("chart-area");
		context = canvas.getContext("2d");
		new Chart(context, {
			type : "bar",
			data : data,
			options : options
		});
	});
</script>