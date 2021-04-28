package acme.entities.workplans;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

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
	
	@NotNull
	protected Boolean isPublic;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	protected Date executionPeriodStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	protected Date executionPeriodEnd;
	
	// -------------- Relationships --------------
	
	@ManyToMany
	protected Collection<Task> tasks;
	
	@Transient
	public Integer getWorkload() {
		Integer res = 0;
		for(final Task t : this.tasks) {
			res += t.getWorkloadHours()*60;
			if(t.getWorkloadFraction()==null) continue;
			res+=t.getWorkloadFraction();
		}
		return res;
	}
}
