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

package com.blurengine.blur.serializers;

import com.blurengine.blur.modules.spawns.Spawn;
import com.blurengine.blur.modules.spawns.serializer.SpawnSerializer.ListSpawnSerializer;

import java.util.ArrayList;
import java.util.Collection;

import pluginbase.config.annotation.SerializeWith;

/**
 * Represents an {@link ArrayList} of {@link Spawn} specifically designed for easy serialization.
 */
@SerializeWith(ListSpawnSerializer.class)
public class SpawnList extends ArrayList<Spawn> {

    public SpawnList(int initialCapacity) {
        super(initialCapacity);
    }

    public SpawnList() {
    }

    public SpawnList(Collection<? extends Spawn> c) {
        super(c);
    }
}