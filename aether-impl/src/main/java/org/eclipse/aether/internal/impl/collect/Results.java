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
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.util.ConfigUtils;

class Results
{

    private final CollectResult result;

    final int maxExceptions;

    final int maxCycles;

    String errorPath;

    public Results( CollectResult result, RepositorySystemSession session )
    {
        this.result = result;
        this.maxExceptions = ConfigUtils.getInteger( session, 50, DefaultDependencyCollector.CONFIG_PROP_MAX_EXCEPTIONS );
        this.maxCycles = ConfigUtils.getInteger( session, 10, DefaultDependencyCollector.CONFIG_PROP_MAX_CYCLES );
    }

    public void addException( Dependency dependency, Exception e, NodeStack nodes )
    {
        if ( maxExceptions < 0 || result.getExceptions().size() < maxExceptions )
        {
            result.addException( e );
            if ( errorPath == null )
            {
                StringBuilder buffer = new StringBuilder( 256 );
                for ( int i = 0; i < nodes.size(); i++ )
                {
                    if ( buffer.length() > 0 )
                    {
                        buffer.append( " -> " );
                    }
                    Dependency dep = nodes.get( i ).getDependency();
                    if ( dep != null )
                    {
                        buffer.append( dep.getArtifact() );
                    }
                }
                if ( buffer.length() > 0 )
                {
                    buffer.append( " -> " );
                }
                buffer.append( dependency.getArtifact() );
                errorPath = buffer.toString();
            }
        }
    }

    public void addCycle( NodeStack nodes, int cycleEntry, Dependency dependency )
    {
        if ( maxCycles < 0 || result.getCycles().size() < maxCycles )
        {
            result.addCycle( new DefaultDependencyCycle( nodes, cycleEntry, dependency ) );
        }
    }

}