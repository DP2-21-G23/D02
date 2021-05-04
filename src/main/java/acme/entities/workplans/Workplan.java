package acme.entities.workplans;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.framework.entities.DomainEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Workplan extends DomainEntity {

	// --------------Serialisation identifier--------------
	
	protected static final long serialVersionUID = 1L;
	
	// -------------- Attributes --------------
	
	@NotBlank
	protected String title;
	
	@NotNull
	protected Boolean isPublic;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	protected Date executionPeriodStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	protected Date executionPeriodEnd;
	
	// -------------- Relationships --------------
	
	@ManyToMany(fetch = FetchType.EAGER)
	protected Collection<Task> tasks;
	
	@ManyToOne(optional=false)
	protected Manager owner;
	
	
	// -------------- Derived Attributes --------------
	
	@Transient
	public String getWorkload() {
		String res = "";
		Integer auxH = 0;
		Integer auxM = 0;
		for(final Task t : this.tasks) {
			auxH += t.getWorkloadHours();
			if(t.getWorkloadFraction()==null) continue;
			auxM+=t.getWorkloadFraction();
		}
		auxH += auxM/60;
		auxM = auxM % 60;
		res = auxH + "h. " + auxM + "min.";
		return res;
	}
}
