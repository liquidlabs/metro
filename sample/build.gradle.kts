plugins {
  alias(libs.plugins.kotlin.multiplatform)
  id("dev.zacsweers.lattice")
}

kotlin {
  jvm()
  sourceSets {
    commonMain { dependencies { implementation(project(":runtime")) } }
    commonTest { dependencies { implementation(libs.kotlin.test) } }
  }
}

lattice { debug.set(true) }

configurations.configureEach {
  resolutionStrategy.dependencySubstitution {
    substitute(module("dev.zacsweers.lattice:runtime")).using(project(":runtime"))
    substitute(module("dev.zacsweers.lattice:runtime-jvm")).using(project(":runtime"))
    substitute(module("dev.zacsweers.lattice:compiler")).using(project(":compiler"))
  }
}
