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

package com.blurengine.blur;

import com.blurengine.blur.session.BlurSession;
import com.blurengine.blur.session.SessionManager;

import lombok.NonNull;

/**
 * Represents a root {@link BlurSession} that is incharge of all the other {@link BlurSession}. There should only exist one instance of this class 
 * across the whole program.
 */
public class RootBlurSession extends BlurSession {

    public RootBlurSession(@NonNull SessionManager manager) {
        super(manager, null);
    }

    @Override
    public void start() {
        getLogger().info("Starting " + getClass().getName());
        this.moduleManager.load();
        this.moduleManager.enable();
        super.start();
    }

    @Override
    public void stop() {
        getLogger().info("Stopping " + getClass().getName());
        super.stop();
        this.moduleManager.disable();
        this.moduleManager.unload();
    }
}
