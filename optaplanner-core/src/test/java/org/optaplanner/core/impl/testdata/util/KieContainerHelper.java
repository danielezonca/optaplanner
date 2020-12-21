/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.mvel.CommonTestMethodBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import com.google.common.io.Resources;

/**
 * Simplifies creating and deploying testing kjars.
 */
public class KieContainerHelper {

    private final KieServices kieServices = KieServices.Factory.get();

    /**
     * Deploys testing kjar containing solver configuration. The kjar already contains testing domain classes and
     * rules for Drools score calculation.
     *
     * @param artifactId artifactId of the kjar to be created
     * @param kmodulePath path to a kmodule.xml
     * @param solverConfigPath path to a solver configuration xml file
     * @param additionalResources additional resources that should be included in the kjar
     * @return releaseId of the created and deployed {@link KieModule}
     * @throws IOException in case ony of the resources cannot be read
     */
    public ReleaseId deployTestdataSolverKjar(String artifactId, String kmodulePath, String solverConfigPath,
            Resource... additionalResources)
            throws IOException {

        if (solverConfigPath != null) {
            Resource solverConfig = buildResource(solverConfigPath,
                    "testdata/kjar/solverConfig.solver");
            List<Resource> allResources = new ArrayList<>();
            allResources.add(solverConfig);
            Collections.addAll(allResources, additionalResources);
            return deployTestdataKjar(artifactId, kmodulePath, allResources.toArray(new Resource[0]));
        } else {
            return deployTestdataKjar(artifactId, kmodulePath, additionalResources);
        }
    }

    /**
     * Deploys testing kjar containing benchmark configuration. The kjar already contains testing domain classes and
     * rules for Drools score calculation.
     *
     * @param artifactId artifactId of the kjar to be created
     * @param kmodulePath path to a kmodule.xml
     * @param benchmarkConfigPath path to a benchmark configuration xml file
     * @return releaseId of the created and deployed {@link KieModule}
     * @throws IOException in case ony of the resources cannot be read
     */
    public ReleaseId deployTestdataBenchmarkKjar(String artifactId, String kmodulePath, String benchmarkConfigPath)
            throws IOException {

        if (benchmarkConfigPath != null) {
            Resource solverConfig = buildResource(benchmarkConfigPath,
                    "testdata/kjar/benchmarkConfig.solver");
            return deployTestdataKjar(artifactId, kmodulePath, solverConfig);
        } else {
            return deployTestdataKjar(artifactId, kmodulePath);
        }
    }

    /**
     * Deploys testing containing testing domain classes and rules for Drools score calculation.
     *
     * @param artifactId artifactId of the kjar to be created
     * @param kmodulePath path to a kmodule.xml
     * @param additionalResources additional resources that should be included in the kjar
     * @return releaseId of the created and deployed {@link KieModule}
     * @throws IOException in case ony of the resources cannot be read
     */
    public ReleaseId deployTestdataKjar(String artifactId, String kmodulePath, Resource... additionalResources)
            throws IOException {
        ReleaseId releaseId = kieServices.newReleaseId("org.optaplanner.core.test", artifactId, "1.0.0");

        String kmodule = readResourceToString(kmodulePath);
        Resource valueClass = buildResource(
                "org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataValue.java",
                "testdata/kjar/ClassloadedTestdataValue.java");
        Resource entityClass = buildResource(
                "org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataEntity.java",
                "testdata/kjar/ClassloadedTestdataEntity.java");
        Resource solutionClass = buildResource(
                "org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataSolution.java",
                "testdata/kjar/ClassloadedTestdataSolution.java");
        Resource constraints = buildResource("org/optaplanner/core/api/solver/kieContainerTestdataConstraints.drl",
                "testdata/kjar/constraints.drl");
        Collection<Resource> resources = new ArrayList<>(Arrays.asList(additionalResources));
        resources.add(valueClass);
        resources.add(entityClass);
        resources.add(solutionClass);
        resources.add(constraints);

        CommonTestMethodBase.createAndDeployJar(kieServices, kmodule, releaseId, resources.toArray(new Resource[0]));

        return releaseId;
    }

    public String readResourceToString(String resourceString) throws IOException {
        URL url = Resources.getResource(resourceString);
        return Resources.toString(url, Charset.forName("UTF-8"));
    }

    public Resource buildResource(String resourceString, String targetPath) throws IOException {
        String content = readResourceToString(resourceString);
        Resource resource = kieServices.getResources().newReaderResource(new StringReader(content), "UTF-8");
        resource.setTargetPath(targetPath);
        if (resourceString.endsWith(".java")) {
            resource.setResourceType(ResourceType.JAVA);
        }
        return resource;
    }
}