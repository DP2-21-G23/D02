package acme.features.manager.workplan;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import acme.entities.roles.ManagerRole;
import acme.entities.workplans.Workplan;
import acme.framework.components.BasicCommand;
import acme.framework.controllers.AbstractController;

@Controller
@RequestMapping("/manager-role/workplan/")
public class ManagerRoleWorkplanController extends AbstractController<ManagerRole, Workplan> {

	@Autowired
	protected ManagerRoleWorkplanListService listService;
	
	@Autowired
	protected ManagerRoleWorkplanShowService showService;
	
	@Autowired
	protected ManagerRoleWorkplanCreateService createService;
	
	@Autowired
	protected ManagerRoleWorkplanDeleteService deleteService;
	
	@Autowired
	protected ManagerRoleWorkplanUpdateService updateService;
	
	@PostConstruct
	protected void initialise() {
		super.addBasicCommand(BasicCommand.LIST, this.listService);
		super.addBasicCommand(BasicCommand.SHOW, this.showService);
		super.addBasicCommand(BasicCommand.CREATE, this.createService);
		super.addBasicCommand(BasicCommand.DELETE, this.deleteService);
		super.addBasicCommand(BasicCommand.UPDATE, this.updateService);
	}
}
