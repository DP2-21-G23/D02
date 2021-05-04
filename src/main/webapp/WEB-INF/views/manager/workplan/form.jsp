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

<acme:form>

	<acme:form-textbox code="manager.workplan.form.label.title" path="title"/>
	<jstl:if test="${command != 'create'}">
		<acme:form-textbox code="manager.workplan.form.label.suggestion" path="suggestion" readonly="true"/>
	</jstl:if>
	<acme:form-moment code="manager.workplan.form.label.execution-period-start" path="executionPeriodStart"/>
	<acme:form-moment code="manager.workplan.form.label.execution-period-end" path="executionPeriodEnd"/>
	<jstl:if test="${command != 'create'}">
		<acme:form-textbox code="manager.workplan.form.label.workload" path="workload" readonly="true"/>
	</jstl:if>
	<jstl:if test="${command == 'show' or command == 'update'}">
		<acme:form-textbox code="manager.workplan.form.label.available-tasks" path="validTaskIds" readonly="true"/>
		<acme:form-textbox code="manager.workplan.form.label.tasks" path="modelTasks"/>
	</jstl:if>
	<jstl:if test="${command != 'create'}">
		<acme:form-checkbox code="manager.workplan.form.label.is-public" path="isPublic"/>
		<acme:form-return code="manager.workplan.form.button.tasks" action="/manager/task/list?workplanId=${id}"/>
	</jstl:if>
	<acme:form-submit test="${command == 'create'}" 
		code="manager.workplan.form.button.create" 
		action="/manager/workplan/create"/>
	<acme:form-submit test="${command == 'show'}" 
		code="manager.workplan.form.button.update" 
		action="/manager/workplan/update"/>
		<acme:form-submit test="${command == 'show'}" 
		code="manager.workplan.form.button.delete" 
		action="/manager/workplan/delete"/>
	<acme:form-submit test="${command == 'update'}" 
		code="manager.workplan.form.button.update" 
		action="/manager/workplan/update"/>
	<acme:form-submit test="${command == 'update'}" 
		code="manager.workplan.form.button.delete" 
		action="/manager/workplan/delete"/>
	<acme:form-submit test="${command == 'delete'}" 
		code="manager.workplan.form.button.update" 
		action="/manager/workplan/update"/>
	<acme:form-submit test="${command == 'delete'}" 
		code="manager.workplan.form.button.delete" 
		action="/manager/workplan/delete"/>

	<acme:form-return code="manager.workplan.form.button.return"/>
	
</acme:form>
