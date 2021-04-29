package acme.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.framework.components.Request;
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
	
	public <E> SpamModuleResult checkSpam(final Object obj, final Request<E> request) {
		SpamModuleResult result = new SpamModuleResult();
		Map<String, String> spamWordsMap;
		Set<String> spamWordsSet = new HashSet<String>();
		String content;
		StringBuilder contentConcat = new StringBuilder();
		double spamCount = 0.0;
		double contentSize = 0.0;
		List<Field> fieldAccessibility = new ArrayList<Field>();
		double spamThreshold = this.spamRepository.findThreshold();
		
		try {
			if (!(obj instanceof DomainEntity)){
				throw new IllegalArgumentException("Can only check DomainEntity type objects");
			}
			final Field[] fields = obj.getClass().getDeclaredFields();
			for(final Field f: fields) {
				if(!(f.getType().isAssignableFrom(String.class))) {
					continue;
				}
				spamWordsMap = new HashMap<>(this.spamRepository.findSpamWords());
				for(Map.Entry<String, String> entry : spamWordsMap.entrySet()) {
					spamWordsSet.addAll(Arrays.asList(spamWordsMap.get(entry.getKey()).split(",")));
					spamWordsSet = spamWordsSet.stream().map(SpamModule::processString).collect(Collectors.toSet());					
				}
				SpamModule.makeAccessible(f);
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
		} catch (Exception exc) {
			result.setHasErrors(true);
			SpamModule.log.warning("Could not check spam for the received object");
			if(obj==null) {
				SpamModule.log.warning("The object received is null");
			}
			if(exc instanceof IllegalArgumentException) {
				SpamModule.log.warning("The object received is not an entity");
			}
		} finally {
			fieldAccessibility.forEach(f -> f.setAccessible(false));
		}
		
		result.setSpam(SpamModule.isSpam(spamCount, contentSize, spamThreshold));
	
		return result;
	}
	
    private static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) ||
            !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
        {
            field.setAccessible(true);
        }
    }
    
    private static boolean isSpam(double spamCount, double contentSize, double spamThreshold) {
    	boolean res;
    	if (contentSize<=0.0) {
    		res = false;
    	} else {
    		res = spamCount / contentSize*100>=spamThreshold;
    	}
    	return res;
    }
    
    private static String processString(String s) {
    	return s.trim().replaceAll("\\s+", "").toLowerCase();
    }
}
