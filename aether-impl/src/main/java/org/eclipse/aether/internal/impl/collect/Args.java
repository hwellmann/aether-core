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

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;

class Args
{

    final RepositorySystemSession session;

    final boolean ignoreRepos;

    final boolean premanagedState;

    final RequestTrace trace;

    final DataPool pool;

    NodeStack nodes;

    final DefaultDependencyCollectionContext collectionContext;

    final DefaultVersionFilterContext versionContext;

    final CollectRequest request;


    public Args( RepositorySystemSession session, RequestTrace trace, DataPool pool, NodeStack nodes,
                 DefaultDependencyCollectionContext collectionContext, DefaultVersionFilterContext versionContext,
                 CollectRequest request )
    {
        this.session = session;
        this.request = request;
        this.ignoreRepos = session.isIgnoreArtifactDescriptorRepositories();
        this.premanagedState = ConfigUtils.getBoolean( session, false, DependencyManagerUtils.CONFIG_PROP_VERBOSE );
        this.trace = trace;
        this.pool = pool;
        this.nodes = nodes;
        this.collectionContext = collectionContext;
        this.versionContext = versionContext;
    }

}