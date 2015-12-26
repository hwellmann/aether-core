/*******************************************************************************
 * Copyright (c) 2010, 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.internal.impl.collect;

import java.util.List;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;

/**
 * @see DefaultDependencyCollector
 */
class DefaultDependencyCollectionContext
    implements DependencyCollectionContext
{

    private final RepositorySystemSession session;

    private Artifact artifact;

    private Dependency dependency;

    private List<Dependency> managedDependencies;

    private CollectResult collectResult;

    private RequestTrace trace;

    private Args args;

    private Results results;

    private List<Dependency> dependencies;

    private List<RemoteRepository> repositories;

    private DependencySelector depSelector;

    private DependencyManager depManager;

    private DependencyTraverser depTraverser;

    private VersionFilter verFilter;

    private Version version;

    public DefaultDependencyCollectionContext( RepositorySystemSession session, Artifact artifact,
                                               Dependency dependency, List<Dependency> managedDependencies )
    {
        this.session = session;
        this.artifact = ( dependency != null ) ? dependency.getArtifact() : artifact;
        this.dependency = dependency;
        this.managedDependencies = managedDependencies;
    }

    public void prepareDescent()
    {
        DependencySelector depSelector = session.getDependencySelector();
        DependencyManager depManager = session.getDependencyManager();
        VersionFilter verFilter = session.getVersionFilter();
        setDepSelector( depSelector != null ? depSelector.deriveChildSelector( this ) : null );
        setDepManager( depManager != null ? depManager.deriveChildManager( this ) : null );
        setDepTraverser( depTraverser != null ? depTraverser.deriveChildTraverser( this ) : null );
        setVerFilter( verFilter != null ? verFilter.deriveChildFilter( this ) : null );

    }
    
    public DefaultDependencyCollectionContext createChildContext()
    {
        DefaultDependencyCollectionContext childContext =
                        new DefaultDependencyCollectionContext( getSession(), getArtifact(),
                                                                 getDependency(), getManagedDependencies() );
        childContext.depSelector =
            getDepSelector() != null ? getDepSelector().deriveChildSelector( this ) : null;
        childContext.depManager =
            getDepManager() != null ? getDepManager().deriveChildManager( this ) : null;
        childContext.depTraverser =
            getDepTraverser() != null ? getDepTraverser().deriveChildTraverser( this ) : null;
        childContext.verFilter =
            getVerFilter() != null ? getVerFilter().deriveChildFilter( this ) : null;
        return childContext;
    }

    public RepositorySystemSession getSession()
    {
        return session;
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public Dependency getDependency()
    {
        return dependency;
    }

    public List<Dependency> getManagedDependencies()
    {
        return managedDependencies;
    }

    public CollectResult getCollectResult()
    {
        return collectResult;
    }

    public void setCollectResult( CollectResult collectResult )
    {
        this.collectResult = collectResult;
    }

    public Args getArgs()
    {
        return args;
    }

    public void setArgs( Args args )
    {
        this.args = args;
    }

    public Results getResults()
    {
        return results;
    }

    public void setResults( Results results )
    {
        this.results = results;
    }

    public List<Dependency> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }

    public List<RemoteRepository> getRepositories()
    {
        return repositories;
    }

    public void setRepositories( List<RemoteRepository> repositories )
    {
        this.repositories = repositories;
    }

    public DependencySelector getDepSelector()
    {
        return depSelector;
    }

    public void setDepSelector( DependencySelector depSelector )
    {
        this.depSelector = depSelector;
    }

    public DependencyManager getDepManager()
    {
        return depManager;
    }

    public void setDepManager( DependencyManager depManager )
    {
        this.depManager = depManager;
    }

    public DependencyTraverser getDepTraverser()
    {
        return depTraverser;
    }

    public void setDepTraverser( DependencyTraverser depTraverser )
    {
        this.depTraverser = depTraverser;
    }

    public VersionFilter getVerFilter()
    {
        return verFilter;
    }

    public void setVerFilter( VersionFilter verFilter )
    {
        this.verFilter = verFilter;
    }

    public void setDependency( Dependency dependency )
    {
        artifact = dependency.getArtifact();
        this.dependency = dependency;
    }

    public void setManagedDependencies( List<Dependency> managedDependencies )
    {
        this.managedDependencies = managedDependencies;
    }

    public RequestTrace getTrace()
    {
        return trace;
    }

    public void setTrace( RequestTrace trace )
    {
        this.trace = trace;
    }

    public Version getVersion()
    {
        return version;
    }

    public void setVersion( Version version )
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return String.valueOf( getDependency() );
    }

}
