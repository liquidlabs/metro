pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "lattice"

include(
  ":compiler",
  ":runtime",
  ":sample",
)

includeBuild("gradle-plugin") {
  dependencySubstitution {
    substitute(module("dev.zacsweers.lattice:gradle-plugin")).using(project(":"))
  }
}
