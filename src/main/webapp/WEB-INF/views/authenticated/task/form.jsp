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
	
	<acme:form-textbox code="authenticated.task.form.label.taskId" path="taskId"/>
	<acme:form-textbox code="authenticated.task.form.label.title" path="title"/>
	<acme:form-moment code="authenticated.task.form.label.startMoment" path="startMoment"/>
	<acme:form-moment code="authenticated.task.form.label.endMoment" path="endMoment"/>
	<acme:form-integer code="authenticated.task.form.label.workloadHours" path="workloadHours"/>
	<acme:form-integer code="authenticated.task.form.label.workloadFraction" path="workloadFraction"/>
	<acme:form-textarea code="authenticated.task.form.label.description" path="description"/>
	<acme:form-url code="authenticated.task.form.label.link" path="link"/>
	<acme:form-textbox code="authenticated.task.form.label.owner" path="ownerName"/>
	
	<acme:form-return code="authenticated.task.form.button.return"/>
	
</acme:form>
