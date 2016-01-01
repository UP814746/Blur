/*
 * Copyright 2016 Ali Moghnieh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blurengine.blur.modules.filters;

import com.blurengine.blur.modules.filters.Filter.FilterResponse;
import com.supaham.commons.utils.RandomUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Random;
import java.util.function.Predicate;

import lombok.NonNull;

/**
 * Utility class for using {@link Filter}s.
 *
 * @since 1.0
 */
public class Filters {

    public static final Filter ALWAYS_ALLOW = new StaticFilter(FilterResponse.ALLOW);
    public static final Filter ALWAYS_DENY = new StaticFilter(FilterResponse.DENY);
    public static final Filter ALWAYS_ABSTAIN = new StaticFilter(FilterResponse.ABSTAIN);

    /**
     * Inverts a given {@link Filter}. If the given filter is already inverted, the original (not inverted) {@link Filter} is returned.
     *
     * @param filter filter to inverse
     *
     * @return inverted filter
     */
    public static Filter inverse(@NonNull Filter filter) {
        if (filter instanceof InverseFilter) {
            return ((InverseFilter) filter).filter;
        } else { // TODO handle StaticFilters?
            return new InverseFilter(filter);
        }
    }

    /**
     * Creates a filter that returns {@link FilterResponse#ALLOW} only when the given {@link Filter} returns ALLOW. Otherwise, {@link
     * FilterResponse#DENY} is returned.
     *
     * @param filter filter to create Allow filter with
     *
     * @return Allow filter
     */
    public static Filter allow(@NonNull Filter filter) {
        return new AllowFilter(filter);
    }

    /**
     * Creates a filter that returns {@link FilterResponse#ALLOW} only when the given {@link Filter} returns DENY. Otherwise, {@link
     * FilterResponse#ABSTAIN} is returned.
     *
     * @param filter filter to create Deny filter with
     *
     * @return Deny filter
     */
    public static Filter deny(@NonNull Filter filter) {
        return new DenyFilter(filter);
    }

    public static Filter entityType(@NonNull EntityType entityType) {
        return new EntityTypeFilter(entityType);
    }

    public static Filter random(float min, boolean minEquals, float max, boolean maxEquals) {
        return random(RandomUtils.getRandom(), min, minEquals, max, maxEquals);
    }

    public static Filter random(Random random, float min, boolean minEquals, float max, boolean maxEquals) {
        return new RandomFilter(random, min, minEquals, max, maxEquals);
    }

    public static Filter damageCause(DamageCause damageCause) {
        return new DamageCauseFilter(damageCause);
    }
    
    /* ================================
     * >> FILTER MODIFIERS
     * ================================ */

    private static final class InverseFilter implements Filter {

        private final Filter filter;

        public InverseFilter(Filter filter) {
            this.filter = filter;
        }

        @Override
        public FilterResponse test(Object object) {
            return this.filter.test(object).inverse();
        }
    }

    private static final class OneFilter implements Filter {

        private final Filter[] filters;

        private OneFilter(Filter... filters) {
            this.filters = filters;
        }

        @Override
        public FilterResponse test(Object object) {
            int allowCount = 0;
            FilterResponse result = FilterResponse.ABSTAIN;
            for (Filter filter : filters) {
                FilterResponse test = filter.test(object);
                if (test == FilterResponse.ALLOW) {
                    // more than one allow, fail the test.
                    if (++allowCount > 1) {
                        return FilterResponse.DENY;
                    }
                } else if (test == FilterResponse.DENY) {
                    result = FilterResponse.DENY;
                }
            }
            // allowCount can only be either 1 or 0, more than 1 terminates code in for-loop.
            return allowCount == 1 ? FilterResponse.ALLOW : result;
        }
    }

    private static final class AllowFilter implements Filter {

        private final Filter filter;

        private AllowFilter(Filter filter) {
            this.filter = filter;
        }

        @Override
        public FilterResponse test(Object object) {
            return this.filter.test(object) == FilterResponse.ALLOW ? FilterResponse.ALLOW : FilterResponse.DENY;
        }
    }

    private static final class DenyFilter implements Filter {

        private final Filter filter;

        private DenyFilter(Filter filter) {
            this.filter = filter;
        }

        @Override
        public FilterResponse test(Object object) {
            FilterResponse res = this.filter.test(object);
            return res == FilterResponse.ALLOW ? FilterResponse.DENY : FilterResponse.ABSTAIN;
        }
    }

    private static final class StaticFilter implements Filter {

        private final FilterResponse filterResponse;

        private StaticFilter(FilterResponse filterResponse) {
            this.filterResponse = filterResponse;
        }

        @Override
        public FilterResponse test(Object object) {
            return this.filterResponse;
        }
    }

    private static final class EntityTypeFilter implements Filter {

        private final EntityType entityType;

        public EntityTypeFilter(EntityType entityType) {
            this.entityType = entityType;
        }

        @Override
        public FilterResponse test(Object object) {
            if (object instanceof Entity) {
                return FilterResponse.from(((Entity) object).getType() == this.entityType);
            } else if (object instanceof EntityType) {
                return FilterResponse.from(object == this.entityType);
            } else {
                return FilterResponse.ABSTAIN;
            }
        }
    }

    private static final class RandomFilter implements Filter {

        private final Random random;
        private final Predicate<Float> minPredicate;
        private final Predicate<Float> maxPredicate;

        private RandomFilter(Random random, float min, boolean minEquals, float max, boolean maxEquals) {
            this.random = random;
            this.minPredicate = minEquals ? (f) -> f >= min : (f) -> f > min;
            this.maxPredicate = maxEquals ? (f) -> f >= max : (f) -> f > max;
        }

        @Override
        public FilterResponse test(Object object) {
            float f = this.random.nextFloat();
            return FilterResponse.from(this.minPredicate.test(f) && this.maxPredicate.test(f));
        }
    }

    private static final class DamageCauseFilter implements Filter {

        private final DamageCause damageCause;

        public DamageCauseFilter(DamageCause damageCause) {
            this.damageCause = damageCause;
        }

        @Override
        public FilterResponse test(Object object) {
            if (object instanceof DamageCause) {
                return FilterResponse.from(damageCause == object);
            } else if (object instanceof EntityDamageEvent) {
                return FilterResponse.from(damageCause == ((EntityDamageEvent) object).getCause());
            } else {
                return FilterResponse.ABSTAIN;
            }
        }
    }
}
