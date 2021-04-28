package acme.entities.configurations;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import acme.framework.entities.DomainEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Configuration extends DomainEntity {

    // Serialisation identifier -----------------------------------------------

        protected static final long    serialVersionUID    = 1L;

        // Attributes -------------------------------------------------------------

        @NotNull
        protected HashMap<String, String>    spamWords;

        @Range(min = 0, max = 100)
        protected Double                threshold;

}