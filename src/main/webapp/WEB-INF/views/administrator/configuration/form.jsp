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
	<acme:form-textarea code="administrator.configuration.form.label.spam-words-es" path="spamWordsES" />
	<acme:form-textarea code="administrator.configuration.form.label.spam-words-en" path="spamWordsEN" />
	<acme:form-double code="administrator.configuration.form.label.threshold" path="threshold" />
	
	<acme:form-submit test="${command == 'show'}"
		code="administrator.configuration.form.button.update"
		action="/administrator/configuration/update"/>
		
	<acme:form-submit test="${command == 'update'}"
		code="administrator.configuration.form.button.update"
		action="/administrator/configuration/update"/>
  	<acme:form-return code="administrator.configuration.form.button.return"/>
</acme:form>