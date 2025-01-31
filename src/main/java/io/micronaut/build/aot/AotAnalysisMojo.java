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

import io.micronaut.aot.std.sourcegen.AbstractStaticServiceLoaderSourceGenerator;
import io.micronaut.aot.std.sourcegen.KnownMissingTypesSourceGenerator;
import io.micronaut.build.services.CompilerService;
import io.micronaut.build.services.ExecutorService;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.micronaut.build.aot.AotSampleMojo.AOT_PROPERTIES_FILE_NAME;

/**
 * Invokes the <a href="https://micronaut-projects.github.io/micronaut-aot/latest/guide/">Micronaut AOT</a>
 * optimizer, generating sources/classes and the effective AOT configuration properties file. Refer to the Micronaut
 * AOT documentation for more information.
 */
@Mojo(name = "aot-analysis", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class AotAnalysisMojo extends AbstractMicronautAotCliMojo {

    /**
     * The project's target directory.
     */
    @Parameter(property = "project.baseDir", defaultValue = "${project.build.directory}", required = true)
    private File baseDirectory;

    /**
     * Micronaut AOT configuration file. Run the <a href="aot-sample-mojo.html"><code>aot-sample</code> goal</a> to
     * see all the possible options.
     */
    @Parameter(property = "micronaut.aot.config", defaultValue = AOT_PROPERTIES_FILE_NAME)
    private File configFile;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    public AotAnalysisMojo(CompilerService compilerService, ExecutorService executorService, MavenProject mavenProject,
                           MavenSession mavenSession, RepositorySystem repositorySystem) {
        super(compilerService, executorService, mavenProject, mavenSession, repositorySystem);
    }

    @Override
    protected List<String> getExtraArgs() throws MojoExecutionException {
        List<String> args = new ArrayList<>();
        args.add("--output");
        File outputDirectory = outputFile("generated");
        args.add(outputDirectory.getAbsolutePath());
        File configFile = writeEffectiveConfigFile();
        args.add("--config");
        args.add(configFile.getAbsolutePath());
        return args;
    }

    private File writeEffectiveConfigFile() throws MojoExecutionException {
        File userProvidedFile = this.configFile == null ? new File(baseDirectory, AOT_PROPERTIES_FILE_NAME) : this.configFile;
        Properties props = new Properties();
        if (userProvidedFile.exists()) {
            try (InputStream in = new FileInputStream(userProvidedFile)) {
                props.load(in);
            } catch (IOException e) {
                throw new MojoExecutionException("Unable to parse configuration file", e);
            }
        }
        if (!props.containsKey(KnownMissingTypesSourceGenerator.OPTION.key())) {
            props.put(KnownMissingTypesSourceGenerator.OPTION.key(), String.join(",", Constants.TYPES_TO_CHECK));
        }
        if (!props.containsKey(AbstractStaticServiceLoaderSourceGenerator.SERVICE_TYPES)) {
            props.put(AbstractStaticServiceLoaderSourceGenerator.SERVICE_TYPES, String.join(",", Constants.SERVICE_TYPES));
        }
        File effectiveConfig = outputFile("effective-" + AOT_PROPERTIES_FILE_NAME);
        try (OutputStream out = new FileOutputStream(effectiveConfig)) {
            props.store(out, "Effective AOT configuration");
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to parse configuration file", e);
        }
        return effectiveConfig;
    }

}
