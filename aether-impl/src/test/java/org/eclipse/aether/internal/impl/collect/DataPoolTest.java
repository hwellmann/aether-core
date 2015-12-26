/*******************************************************************************
 * Copyright (c) 2012 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.internal.impl.collect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.util.concurrency.FutureResult;
import org.junit.Test;

public class DataPoolTest
{

    private DataPool newDataPool()
    {
        return new DataPool( new DefaultRepositorySystemSession() );
    }

    @Test
    public void testArtifactDescriptorCaching() throws InterruptedException, ExecutionException
    {
        ArtifactDescriptorRequest request = new ArtifactDescriptorRequest();
        request.setArtifact( new DefaultArtifact( "gid:aid:1" ) );
        ArtifactDescriptorResult result = new ArtifactDescriptorResult( request );
        result.setArtifact( new DefaultArtifact( "gid:aid:2" ) );
        result.addRelocation( request.getArtifact() );
        result.addDependency( new Dependency( new DefaultArtifact( "gid:dep:3" ), "compile" ) );
        result.addManagedDependency( new Dependency( new DefaultArtifact( "gid:mdep:3" ), "runtime" ) );
        result.addRepository( new RemoteRepository.Builder( "test", "default", "http://localhost" ).build() );
        result.addAlias( new DefaultArtifact( "gid:alias:4" ) );
        Future<ArtifactDescriptorResult> futureResult = new FutureResult<ArtifactDescriptorResult>( result ); 

        DataPool pool = newDataPool();
        Object key = pool.toKey( request );
        pool.putDescriptor( key, futureResult );
        Future<ArtifactDescriptorResult> futureCached = pool.getDescriptor( key, request );
        assertNotNull( futureCached );
        ArtifactDescriptorResult cached = futureCached.get();
        assertEquals( result.getArtifact(), cached.getArtifact() );
        assertEquals( result.getRelocations(), cached.getRelocations() );
        assertEquals( result.getDependencies(), cached.getDependencies() );
        assertEquals( result.getManagedDependencies(), cached.getManagedDependencies() );
        assertEquals( result.getRepositories(), cached.getRepositories() );
        assertEquals( result.getAliases(), cached.getAliases() );
    }

}
