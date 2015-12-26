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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactProperties;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

public class DependencyCollectionUtils
{

    static DefaultDependencyNode addDependencyNode( DependencyNode parent, List<Artifact> relocations,
                                                    PremanagedDependency preManaged, VersionRangeResult rangeResult,
                                                    Version version, Dependency d, Collection<Artifact> aliases,
                                                    List<RemoteRepository> repos, String requestContext )
    {
        DefaultDependencyNode child = new DefaultDependencyNode( d );
        preManaged.applyTo( child );
        child.setRelocations( relocations );
        child.setVersionConstraint( rangeResult.getVersionConstraint() );
        child.setVersion( version );
        child.setAliases( aliases );
        child.setRepositories( repos );
        child.setRequestContext( requestContext );
        parent.getChildren().add( child );
        return child;
    }

    static DefaultDependencyNode createDependencyNode( DependencyNode parent, List<Artifact> relocations,
                                                       PremanagedDependency preManaged, VersionRangeResult rangeResult,
                                                       Version version, Dependency d,
                                                       ArtifactDescriptorResult descriptorResult,
                                                       DependencyNode cycleNode )
    {
        DefaultDependencyNode child =
            addDependencyNode( parent, relocations, preManaged, rangeResult, version, d, descriptorResult.getAliases(),
                               cycleNode.getRepositories(), cycleNode.getRequestContext() );
        child.setChildren( cycleNode.getChildren() );
        return child;
    }

    static ArtifactDescriptorRequest createArtifactDescriptorRequest( Args args, List<RemoteRepository> repositories,
                                                                      Dependency d )
    {
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( d.getArtifact() );
        descriptorRequest.setRepositories( repositories );
        descriptorRequest.setRequestContext( args.request.getRequestContext() );
        descriptorRequest.setTrace( args.trace );
        return descriptorRequest;
    }

    static VersionRangeRequest createVersionRangeRequest( Args args, List<RemoteRepository> repositories,
                                                          Dependency dependency )
    {
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( dependency.getArtifact() );
        rangeRequest.setRepositories( repositories );
        rangeRequest.setRequestContext( args.request.getRequestContext() );
        rangeRequest.setTrace( args.trace );
        return rangeRequest;
    }

    static boolean isLackingDescriptor( Artifact artifact )
    {
        return artifact.getProperty( ArtifactProperties.LOCAL_PATH, null ) != null;
    }

    static List<RemoteRepository> getRemoteRepositories( ArtifactRepository repository,
                                                         List<RemoteRepository> repositories )
    {
        if ( repository instanceof RemoteRepository )
        {
            return Collections.singletonList( (RemoteRepository) repository );
        }
        if ( repository != null )
        {
            return Collections.emptyList();
        }
        return repositories;
    }

    static List<? extends Version> filterVersions( Dependency dependency, VersionRangeResult rangeResult,
                                                   VersionFilter verFilter, DefaultVersionFilterContext verContext )
                                                       throws VersionRangeResolutionException
    {
        if ( rangeResult.getVersions().isEmpty() )
        {
            throw new VersionRangeResolutionException( rangeResult, "No versions available for "
                + dependency.getArtifact() + " within specified range" );
        }

        List<? extends Version> versions;
        if ( verFilter != null && rangeResult.getVersionConstraint().getRange() != null )
        {
            verContext.set( dependency, rangeResult );
            try
            {
                verFilter.filterVersions( verContext );
            }
            catch ( RepositoryException e )
            {
                throw new VersionRangeResolutionException( rangeResult, "Failed to filter versions for "
                    + dependency.getArtifact() + ": " + e.getMessage(), e );
            }
            versions = verContext.get();
            if ( versions.isEmpty() )
            {
                throw new VersionRangeResolutionException( rangeResult, "No acceptable versions for "
                    + dependency.getArtifact() + ": " + rangeResult.getVersions() );
            }
        }
        else
        {
            versions = rangeResult.getVersions();
        }
        return versions;
    }
    
    static RepositorySystemSession optimizeSession( RepositorySystemSession session )
    {
        DefaultRepositorySystemSession optimized = new DefaultRepositorySystemSession( session );
        optimized.setArtifactTypeRegistry( CachingArtifactTypeRegistry.newInstance( session ) );
        return optimized;
    }

    static String getId( Dependency d )
    {
        Artifact a = d.getArtifact();
        return a.getGroupId() + ':' + a.getArtifactId() + ':' + a.getClassifier() + ':' + a.getExtension();
    }
}
