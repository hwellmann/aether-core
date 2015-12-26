/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.internal.impl.collect;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

class DependencyContext
{
    Dependency origDependency;
    DefaultDependencyCollectionContext context;
    List<Artifact> relocations;
    boolean disableVersionManagement;
    Args args;
    PremanagedDependency preManaged;
    boolean traverse;
    VersionRangeResult rangeResult;
    Version version; 
    Artifact originalArtifact;
    Dependency managedDependency; 
    Future<ArtifactDescriptorResult> futureDescriptorResult;
    ArtifactDescriptorResult descriptorResult;
    
    public DependencyContext()
    {
        // empty
    }
    
    public DependencyContext( DefaultDependencyCollectionContext context, Dependency dependency )
    {
        this.context = context;
        this.origDependency = dependency;
        this.relocations = Collections.emptyList();
        this.disableVersionManagement = false;
        this.args = context.getArgs();
    }
    
}
