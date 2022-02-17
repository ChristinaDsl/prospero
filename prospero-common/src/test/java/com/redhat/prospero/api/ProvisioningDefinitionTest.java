/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.prospero.api;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;
import org.wildfly.channel.UnresolvedMavenArtifactException;

public class ProvisioningDefinitionTest {

   @Test(expected = UnresolvedMavenArtifactException.class)
   public void resolveChannelFileThrowsExceptionIfNoVersionsFound() throws Exception {
      final DefaultArtifact defaultArtifact = new DefaultArtifact("org.test", "artifactId", "channel", "yaml", "[" + 1.0 + ",)");
      final RemoteRepository repository = new RemoteRepository.Builder("test", "default", this.getClass().getResource("/").toString()).build();

      ProvisioningDefinition.resolveChannelFile(defaultArtifact, repository);
   }
}