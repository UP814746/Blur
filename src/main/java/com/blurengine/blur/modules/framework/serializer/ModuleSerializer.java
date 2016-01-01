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

package com.blurengine.blur.modules.framework.serializer;

import com.google.common.base.Preconditions;

import com.blurengine.blur.modules.framework.BlurSerializer;
import com.blurengine.blur.modules.framework.Module;
import com.blurengine.blur.modules.framework.ModuleInfo;
import com.blurengine.blur.modules.framework.ModuleLoader;
import com.blurengine.blur.modules.includes.IncludesModule.IncludesData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import pluginbase.config.serializers.SerializerSet;

public class ModuleSerializer implements BlurSerializer<Module> {

    private final ModuleLoader moduleLoader;

    public ModuleSerializer(ModuleLoader moduleLoader) {
        this.moduleLoader = moduleLoader;
    }

    @Override
    public Module deserialize(@Nullable Object serialized, @NotNull Class wantedType, @NotNull SerializerSet serializerSet) {
        if (serialized == null) {
            return null;
        }

        String moduleName;
        Optional<Object> data = Optional.empty();
        // Module is represented by name:data, where data is only relayed to the ModuleLoader.
        if (serialized instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) serialized;
            Preconditions.checkArgument(map.size() == 1, "Serialized module must be a map of one entry.");
            Entry<String, Object> next = map.entrySet().iterator().next();
            moduleName = next.getKey().trim();
            data = Optional.ofNullable(next.getValue());
        } else if (serialized instanceof String) { // The module is represented only by its name.
            moduleName = serialized.toString().trim();
        } else {
            throw new IllegalStateException("Unexpected module data type " + serialized.getClass().getName());
        }

        if (moduleName.isEmpty()) {
            return null;
        }

        // quick include
        if (moduleName.startsWith("+")) {
            return moduleLoader.createModuleQuickParse(new IncludesData(), moduleName.substring(1));
        } else {
            ModuleInfo modInfo = ModuleLoader.getModuleInfoByName(moduleName);
            // If no module was found throw an error and suggest similar named modules.
            if (modInfo == null) {
                throw new ModuleNotFoundException(moduleName);
            }
            return moduleLoader.createModule(modInfo, data.orElse(null));
        }
    }

}
