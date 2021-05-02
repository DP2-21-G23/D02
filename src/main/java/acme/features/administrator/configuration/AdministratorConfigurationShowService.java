
package acme.features.administrator.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.configurations.Configuration;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Administrator;
import acme.framework.services.AbstractShowService;

@Service
public class AdministratorConfigurationShowService implements AbstractShowService<Administrator, Configuration> {

	@Autowired
	private AdministratorConfigurationRepository		repository;

	@Override
	public boolean authorise(final Request<Configuration> request) {
		assert request != null;

		return true;
	}

	@Override
	public void unbind(final Request<Configuration> request, final Configuration entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "spamWords", "threshold");
		
		model.setAttribute("spamWordsES", entity.getSpamWords().get("es"));
		model.setAttribute("spamWordsEN", entity.getSpamWords().get("en"));
	}

	@Override
	public Configuration findOne(final Request<Configuration> request) {

		Configuration result;

		result = this.repository.findOne();

		return result;
	}

}
