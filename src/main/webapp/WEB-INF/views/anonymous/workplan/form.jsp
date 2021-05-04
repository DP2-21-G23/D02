<%--
- form.jsp
-
- Copyright (C) 2012-2021 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<acme:form readonly="true">

	<acme:form-textbox code="anonymous.workplan.form.label.title" path="title"/>
	<acme:form-moment code="anonymous.workplan.form.label.execution-period-start" path="executionPeriodStart"/>
	<acme:form-moment code="anonymous.workplan.form.label.execution-period-end" path="executionPeriodEnd"/>
	<acme:form-textbox code="anonymous.workplan.form.label.workload" path="workload"/>
	<acme:form-checkbox code="anonymous.workplan.form.label.is-public" path="isPublic"/>
	
	<acme:form-return code="anonymous.workplan.form.button.tasks" action="/anonymous/task/list?workplanId=${id}"/>
	
	<acme:form-return code="anonymous.workplan.form.button.return"/>
	
</acme:form>
