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

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;

class PremanagedDependency
{
    final String premanagedVersion;

    final String premanagedScope;

    final Boolean premanagedOptional;

    final int managedBits;

    final Dependency managedDependency;

    final boolean premanagedState;

    PremanagedDependency( String premanagedVersion, String premanagedScope, Boolean premanagedOptional,
                          int managedBits, Dependency managedDependency, boolean premanagedState )
    {
        this.premanagedVersion = premanagedVersion;
        this.premanagedScope = premanagedScope;
        this.premanagedOptional = premanagedOptional;
        this.managedBits = managedBits;
        this.managedDependency = managedDependency;
        this.premanagedState = premanagedState;
    }

    static PremanagedDependency create( DependencyManager depManager, Dependency dependency,
                                        boolean disableVersionManagement, boolean premanagedState )
    {
        DependencyManagement depMngt = depManager != null ? depManager.manageDependency( dependency ) : null;

        int managedBits = 0;
        String premanagedVersion = null;
        String premanagedScope = null;
        Boolean premanagedOptional = null;

        if ( depMngt != null )
        {
            if ( depMngt.getVersion() != null && !disableVersionManagement )
            {
                Artifact artifact = dependency.getArtifact();
                premanagedVersion = artifact.getVersion();
                dependency = dependency.setArtifact( artifact.setVersion( depMngt.getVersion() ) );
                managedBits |= DependencyNode.MANAGED_VERSION;
            }
            if ( depMngt.getProperties() != null )
            {
                Artifact artifact = dependency.getArtifact();
                dependency = dependency.setArtifact( artifact.setProperties( depMngt.getProperties() ) );
                managedBits |= DependencyNode.MANAGED_PROPERTIES;
            }
            if ( depMngt.getScope() != null )
            {
                premanagedScope = dependency.getScope();
                dependency = dependency.setScope( depMngt.getScope() );
                managedBits |= DependencyNode.MANAGED_SCOPE;
            }
            if ( depMngt.getOptional() != null )
            {
                premanagedOptional = dependency.isOptional();
                dependency = dependency.setOptional( depMngt.getOptional() );
                managedBits |= DependencyNode.MANAGED_OPTIONAL;
            }
            if ( depMngt.getExclusions() != null )
            {
                dependency = dependency.setExclusions( depMngt.getExclusions() );
                managedBits |= DependencyNode.MANAGED_EXCLUSIONS;
            }
        }
        return new PremanagedDependency( premanagedVersion, premanagedScope, premanagedOptional, managedBits,
                                         dependency, premanagedState );
    }

    public void applyTo( DefaultDependencyNode child )
    {
        child.setManagedBits( managedBits );
        if ( premanagedState )
        {
            child.setData( DependencyManagerUtils.NODE_DATA_PREMANAGED_VERSION, premanagedVersion );
            child.setData( DependencyManagerUtils.NODE_DATA_PREMANAGED_SCOPE, premanagedScope );
            child.setData( DependencyManagerUtils.NODE_DATA_PREMANAGED_OPTIONAL, premanagedOptional );
        }
    }
}