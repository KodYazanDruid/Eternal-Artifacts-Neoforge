package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTagEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTypeEntry;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Predicate;

public interface EntityFilterable {
	EntityPredicateEntry getEntityFilter();
	void setEntityFilter(EntityPredicateEntry filter);
	Predicate<EntityPredicateEntry.EntityPredicate> getFilterValidator();
	
	List<EntityTypeEntry> getEntityTypeEntries();
	void setEntityTypeEntries(List<EntityTypeEntry> entries);
	
	List<EntityTagEntry> getEntityTagEntries();
	void setEntityTagEntries(List<EntityTagEntry> entries);
	
	default boolean matchesAllFilters(Entity entity) {
		if (entity == null) return false;
		EntityPredicateEntry predicateFilter = getEntityFilter();
		boolean isWhitelist = predicateFilter.isWhitelist();
		EntityPredicateEntry.PredicateMode mode = predicateFilter.getMode();
		
		List<EntityTypeEntry> typeEntries = getEntityTypeEntries();
		List<EntityTagEntry> tagEntries = getEntityTagEntries();
		
		boolean hasPredicates = !predicateFilter.isEmpty();
		boolean hasTypes = !typeEntries.isEmpty();
		boolean hasTags = !tagEntries.isEmpty();
		
		if (!hasPredicates && !hasTypes && !hasTags) return isWhitelist;
		
		boolean predicateResult = !hasPredicates || predicateFilter.matchesRaw(entity);
		boolean typeResult = !hasTypes || typeEntries.stream().anyMatch(e -> e.matchesRaw(entity));
		boolean tagResult = !hasTags || tagEntries.stream().anyMatch(e -> e.matchesRaw(entity));
		
		boolean result;
		if (mode == EntityPredicateEntry.PredicateMode.ALL) {
			result = predicateResult && typeResult && tagResult;
		} else {
			boolean anyMatch = false;
			if (hasPredicates) anyMatch = predicateResult;
			if (hasTypes) anyMatch = anyMatch || typeResult;
			if (hasTags) anyMatch = anyMatch || tagResult;
			result = anyMatch;
		}
		
		return isWhitelist == result;
	}
}
