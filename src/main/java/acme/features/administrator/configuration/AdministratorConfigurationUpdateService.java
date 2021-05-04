/*
 * ManagerTaskUpdateService.java
 *
 * Copyright (C) 2012-2021 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.administrator.configuration;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.configurations.Configuration;
import acme.framework.components.Errors;
import acme.framework.components.HttpMethod;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.components.Response;
import acme.framework.entities.Administrator;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractUpdateService;
import acme.utilities.SpamRepository;

@Service
public class AdministratorConfigurationUpdateService implements AbstractUpdateService<Administrator, Configuration> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorConfigurationRepository repository;
	
	@Autowired
	protected SpamRepository spamRepository;
	
	// AbstractUpdateService<Administrator, Configuration> interface ---------------


	@Override
	public boolean authorise(final Request<Configuration> request) {
		assert request != null;

		return true;
	}

	@Override
	public void bind(final Request<Configuration> request, final Configuration entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Configuration> request, final Configuration entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "spamWords", "threshold");
	}

	@Override
	public Configuration findOne(final Request<Configuration> request) {
		assert request != null;

		final Configuration result;		
		
		result = this.repository.findOne();

		return result;
	}

	@Override
	public void validate(final Request<Configuration> request, final Configuration entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		if(!errors.hasErrors("spamWordsES")) {
			final boolean esVacioES = request.getModel().getString("spamWordsES").equals("")||request.getModel().getString("spamWordsES")==null;
			errors.state(request, !esVacioES, "spamWordsES", "administrator.configuration.form.error.spamWords-vacio");
		}
		
		if(!errors.hasErrors("spamWordsEN")) {
			final boolean esVacioEN = request.getModel().getString("spamWordsEN").equals("")||request.getModel().getString("spamWordsEN")==null;
			errors.state(request, !esVacioEN, "spamWordsEN", "administrator.configuration.form.error.spamWords-vacio");
		}
		
		
		
	}

	@Override
	public void update(final Request<Configuration> request, final Configuration entity) {
		assert request != null;
		assert entity != null;
		
		final String spamWordsES = request.getModel().getString("spamWordsES");
		final String spamWordsEN = request.getModel().getString("spamWordsEN");
		
		final HashMap<String, String> spamWords = entity.getSpamWords();
		spamWords.replace("es", spamWordsES);
		spamWords.replace("en", spamWordsEN);
		
		entity.setSpamWords(spamWords);
		
		this.repository.save(entity);
	}

	@Override
	public void onSuccess(final Request<Configuration> request, final Response<Configuration> response) {
		assert request != null;
		assert response != null;

		if (request.isMethod(HttpMethod.POST)) {
			PrincipalHelper.handleUpdate();
		}
	}

}
