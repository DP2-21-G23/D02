
<%--
- list.jsp
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

<acme:list>
	<acme:list-column code="manager.workplan.list.label.title" path="title" width="20%"/>
	<acme:list-column code="manager.workplan.list.label.execution-period-start" path="executionPeriodStart" width="20%"/>
	<acme:list-column code="manager.workplan.list.label.execution-period-end" path="executionPeriodEnd" width="20%"/>
	<acme:list-column code="manager.workplan.list.label.workload" path="workload" width="20%"/>
</acme:list>
