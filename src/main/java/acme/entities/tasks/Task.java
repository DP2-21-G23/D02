package acme.entities.tasks;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import acme.entities.roles.Manager;
import acme.framework.entities.DomainEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task extends DomainEntity {
	
	//	Serialisation identifier ---------------------------

	protected static final long	serialVersionUID	= 1L;
	
	//  Attributes
	
	@NotBlank
	@Length(max=10)
	@Column(unique = true)
	protected String taskId;
	
	@NotBlank
	@Length(max = 80)
	protected String title;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	protected Date startMoment;
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	protected Date endMoment;
	
	@NotNull
	protected Integer workloadHours;
	
	@Range(min = 0, max = 59)
	protected Integer workloadFraction;
	
	@NotBlank
	@Column(length = 500)
	@Length(max = 500)
	protected String description;
	
	@URL
	protected String link;
	
	@NotNull
	protected Boolean isPublic;

	// -------------- Relationships --------------
	
	@ManyToOne(optional=false)
	protected Manager owner;

}
