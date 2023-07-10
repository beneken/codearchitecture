package com.example.internaldependencies;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.example.internaldependencies")
class LayerTest {
        @ArchTest
        static final ArchRule layer_dependencies_are_respected = layeredArchitecture().consideringAllDependencies()

                .layer("controller").definedBy("com.example.internaldependencies.controller..")
                .layer("services").definedBy("com.example.internaldependencies.services..")
                .layer("repositories").definedBy("com.example.internaldependencies.repositories..")

                .whereLayer("controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("services").mayOnlyBeAccessedByLayers("controller")
                .whereLayer("repositories").mayOnlyBeAccessedByLayers("services", "controller" );
}