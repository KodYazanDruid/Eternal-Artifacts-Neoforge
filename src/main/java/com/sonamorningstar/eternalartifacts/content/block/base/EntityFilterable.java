package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;

import java.util.function.Predicate;

public interface EntityFilterable {
	EntityPredicateEntry getEntityFilter();
	void setEntityFilter(EntityPredicateEntry filter);
	Predicate<EntityPredicateEntry.EntityPredicate> getFilterValidator();
}
