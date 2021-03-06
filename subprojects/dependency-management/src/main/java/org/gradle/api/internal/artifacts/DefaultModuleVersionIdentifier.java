/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts;

import com.google.common.base.Objects;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;

public class DefaultModuleVersionIdentifier implements ModuleVersionIdentifier {
    private static final Interner<DefaultModuleVersionIdentifier> INSTANCES_INTERNER = Interners.newStrongInterner();
    private final DefaultModuleIdentifier id;
    private final String version;
    private String displayName;
    private final int hashCode;

    private DefaultModuleVersionIdentifier(String group, String name, String version) {
        assert group != null : "group cannot be null";
        assert name != null : "name cannot be null";
        assert version != null : "version cannot be null";
        this.id = DefaultModuleIdentifier.of(group, name);
        this.version = version;
        this.hashCode = calculateHashCode();
    }

    private DefaultModuleVersionIdentifier(ModuleIdentifier id, String version) {
        this.id = DefaultModuleIdentifier.of(id.getGroup(), id.getName());
        this.version = version;
        this.hashCode = calculateHashCode();
    }

    public static DefaultModuleVersionIdentifier of(ModuleIdentifier id, String version) {
        DefaultModuleVersionIdentifier instance = new DefaultModuleVersionIdentifier(id, version);
        return INSTANCES_INTERNER.intern(instance);
    }

    public static DefaultModuleVersionIdentifier of(String group, String name, String version) {
        DefaultModuleVersionIdentifier instance = new DefaultModuleVersionIdentifier(group, name, version);
        return INSTANCES_INTERNER.intern(instance);
    }

    public String getGroup() {
        return id.getGroup();
    }

    public String getName() {
        return id.getName();
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        if (displayName == null) {
            displayName = createDisplayName();
        }
        return displayName;
    }

    private String createDisplayName() {
        String group = id.getGroup();
        String module = id.getName();
        StringBuilder builder = new StringBuilder(group.length() + module.length() + version.length() + 2);
        builder.append(group);
        builder.append(":");
        builder.append(module);
        builder.append(":");
        builder.append(version);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        DefaultModuleVersionIdentifier other = (DefaultModuleVersionIdentifier) obj;
        if (hashCode() != other.hashCode()) {
            return false;
        }
        if (!id.equals(other.id)) {
            return false;
        }
        if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return Objects.hashCode(id, version);
    }

    public ModuleIdentifier getModule() {
        return id;
    }

    public static ModuleVersionIdentifier newId(Module module) {
        return of(module.getGroup(), module.getName(), module.getVersion());
    }

    public static ModuleVersionIdentifier newId(String group, String name, String version) {
        return of(group, name, version);
    }

    public static ModuleVersionIdentifier newId(ModuleComponentIdentifier componentId) {
        return of(componentId.getGroup(), componentId.getModule(), componentId.getVersion());
    }
}
