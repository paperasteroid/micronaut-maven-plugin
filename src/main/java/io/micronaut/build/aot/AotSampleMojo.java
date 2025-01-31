/*
 * Copyright 2003-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.build.aot;

import io.micronaut.build.services.CompilerService;
import io.micronaut.build.services.ExecutorService;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Generates a sample aot.properties showcasing all the possible values along with a description.
 */
@Mojo(name = "aot-sample", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class AotSampleMojo extends AbstractMicronautAotCliMojo {

    public static final String AOT_PROPERTIES_FILE_NAME = "aot.properties";

    @Inject
    public AotSampleMojo(CompilerService compilerService, ExecutorService executorService, MavenProject mavenProject,
                         MavenSession mavenSession, RepositorySystem repositorySystem) {
        super(compilerService, executorService, mavenProject, mavenSession, repositorySystem);
    }

    @Override
    protected List<String> getExtraArgs() {
        return Arrays.asList(
                "--config",
                outputFile(AOT_PROPERTIES_FILE_NAME).getAbsolutePath()
        );
    }

    @Override
    protected void onSuccess(File outputDir) {
        File sampleFile = new File(outputDir, AOT_PROPERTIES_FILE_NAME);
        if (sampleFile.exists()) {
            getLog().info("Sample configuration file written to " + sampleFile);
        }
    }
}
