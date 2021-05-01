package acme.utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.framework.entities.DomainEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class SpamModule {

	@Autowired
	private final SpamRepository spamRepository;
	
	public SpamModule(final SpamRepository spamRepository) {
		this.spamRepository = spamRepository;
	}
	
	@Getter
	@Setter
	public class SpamModuleResult{
		boolean isSpam;
		boolean hasErrors;
		
		public SpamModuleResult() {
			this.isSpam = false;
			this.hasErrors = false;
		}
	}
	
	/**
     * Return a SpamModuleResult object with information on whether the object to be validated is spam
     * and has errors, or not. It looks for spam in String attributes any given object, regardless of 
     * its type. If there are errors or the object is not an entity, it marks it as not being spam and 
     * logs the corresponding information.
     * 
     * @param obj Object to check
     */
	public SpamModuleResult checkSpam(final Object obj) {
		final SpamModuleResult result = new SpamModuleResult();
		Map<String, String> spamWordsMap;
		Set<String> spamWordsSet = new HashSet<String>();
		String content;
		final StringBuilder contentConcat = new StringBuilder();
		double spamCount = 0.0;
		double contentSize = 0.0;
		final List<Field> fieldAccessibility = new ArrayList<Field>();
		final double spamThreshold = this.spamRepository.findThreshold();
		
		try {
			if (!(obj instanceof DomainEntity)){
				SpamModule.log.warning("The object received is not an entity");
			}
			final Field[] fields = obj.getClass().getDeclaredFields();
			
			spamWordsMap = new HashMap<>(this.spamRepository.findSpamWords());
			for(final Map.Entry<String, String> entry : spamWordsMap.entrySet()) {
				spamWordsSet.addAll(Arrays.asList(spamWordsMap.get(entry.getKey()).split(",")));
				spamWordsSet = spamWordsSet.stream().map(SpamModule::processString).collect(Collectors.toSet());					
			}
			
			for(final Field f: fields) {
				if(!(f.getType().isAssignableFrom(String.class))) {
					continue;
				}
				SpamModule.changeAccesibility(f);
				fieldAccessibility.add(f);
				if(f.get(obj)!=null) {
					contentConcat.append(SpamModule.processString((String)f.get(obj)));
				}
			}
			content = contentConcat.toString();
			contentSize = 1.0*content.length();
			for (final String spamWord:spamWordsSet) {
				Integer loopCount = 0;
				loopCount += content.split(spamWord, -1).length -1;
				spamCount += loopCount * spamWord.length();
			}
		} catch (final Exception exc) {
			result.setHasErrors(true);
			SpamModule.log.warning("Could not check spam for the received object");
			if(obj==null) {
				SpamModule.log.warning("The object received is null");
			}
		} finally {
			fieldAccessibility.forEach(SpamModule::changeAccesibility);
		}
		
		result.setSpam(SpamModule.isSpam(spamCount, contentSize, spamThreshold));
	
		return result;
	}
	
	/**
     * Change the accessibility of the provided field.
     */
    private static void changeAccesibility(final Field field) {
        field.setAccessible(!field.isAccessible());
    }
    
    /**
     * Return true if the amount of text considered as spam exceeds the system's spam threshold,
     * false otherwise or if contentSize is 0 or negative.
     */
    private static boolean isSpam(final double spamCount, final double contentSize, final double spamThreshold) {
    	return contentSize>0.0 && (spamCount/contentSize*100>=spamThreshold);
    }
    
    /**
     * Return the String, but trimmed, without spaces and in lower case.
     */
    private static String processString(final String string) {
    	return string.trim().replaceAll("\\s+", "").toLowerCase();
    }
}
