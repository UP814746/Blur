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

package com.blurengine.blur.modules.extents;

import com.google.common.base.Preconditions;

import com.blurengine.blur.modules.filters.Filter;
import com.supaham.commons.bukkit.utils.ImmutableVector;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Iterator;

import javax.annotation.Nonnull;

/**
 * Represents an extent typically contained in a world. Extent extends {@link Filter} which allows for very convenient filter tests. Extent also has
 * an {@link Iterator} of {@link BlockVector} for accessing each BlockVector the Extent covers. Each Extent implementation has it's own implementation
 * of {@link #iterator()} so please be sure to read the specific documentation for more information.
 */
public interface Extent extends Filter, Iterable<BlockVector> {

    /**
     * Returns whether an {@link Entity} is within this {@link Extent}.
     *
     * @param entity entity to test
     *
     * @return whether the {@code entity} is within this extent
     */
    default boolean contains(@Nonnull Entity entity) {
        return contains(Preconditions.checkNotNull(entity, "entity cannot be null.").getLocation());
    }

    /**
     * Returns whether a {@link Block} is within this {@link Extent}.
     *
     * @param block block to test
     *
     * @return whether the {@code block} is within this extent
     */
    default boolean contains(@Nonnull Block block) {
        return contains(Preconditions.checkNotNull(block, "block cannot be null.").getLocation());
    }

    /**
     * Returns whether a {@link Vector} is within this {@link Extent}.
     *
     * @param vector vector to test
     *
     * @return whether the {@code vector} is within this extent
     */
    default boolean contains(@Nonnull Vector vector) {
        Preconditions.checkNotNull(vector, "vector cannot be null.");
        return contains(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Returns whether an {@link ImmutableVector} is within this {@link Extent}.
     *
     * @param immutableVector immutable vector to test
     *
     * @return whether the {@code immutableVector} is within this extent
     */
    default boolean contains(@Nonnull ImmutableVector immutableVector) {
        Preconditions.checkNotNull(immutableVector, "immutableVector cannot be null.");
        return contains(immutableVector.getX(), immutableVector.getY(), immutableVector.getZ());
    }

    /**
     * Returns whether a {@link Location} is within this {@link Extent}.
     *
     * @param location location to test
     *
     * @return whether the {@code location} is within this extent
     */
    default boolean contains(@Nonnull Location location) {
        Preconditions.checkNotNull(location, "location cannot be null.");
        return contains(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Returns whether this {@link Extent} contains a vector of three components.
     *
     * @param x x component
     * @param y y component
     * @param z z component
     *
     * @return whether {@code x}, {@code y}, {@code z} are within this extent
     */
    boolean contains(double x, double y, double z);

    /**
     * Returns whether this Extent has any boundaries.
     *
     * @return true if this extent has any boundaries
     */
    default boolean isInfinite() {
        return true;
    }

    @Override
    default FilterResponse test(Object object) {
        if (object instanceof Entity) {
            return FilterResponse.from(contains((Entity) object));
        } else if (object instanceof Block) {
            return FilterResponse.from(contains((Block) object));
        } else if (object instanceof Vector) {
            return FilterResponse.from(contains((Vector) object));
        } else if (object instanceof ImmutableVector) {
            return FilterResponse.from(contains((ImmutableVector) object));
        } else if (object instanceof Location) {
            return FilterResponse.from(contains((Location) object));
        }
        return FilterResponse.ABSTAIN;
    }
}
