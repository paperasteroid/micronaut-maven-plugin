package io.micronaut.build.examples;

import io.micronaut.configuration.picocli.PicocliRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "run2", description = "...",
        mixinStandardHelpOptions = true)
public class RunCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(RunCommand.class, args);
    }

    public void run() {
        boolean optimized = false;
        try {
            Class.forName("io.micronaut.build.examples.AOTApplicationContextConfigurer");
            optimized = true;
        } catch (Exception ex) {
            // ignore
        }
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
            if (optimized) {
                System.out.println("Running with AOT optimizations");
            }
        }
    }
}
