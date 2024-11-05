package dev.zacsweers.lattice.compiler.transformers

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import dev.zacsweers.lattice.compiler.LatticeCompilerTest
import dev.zacsweers.lattice.internal.Factory
import java.util.concurrent.Callable
import org.junit.Ignore
import org.junit.Test

class InjectConstructorTransformerTest : LatticeCompilerTest() {

  @Test
  fun simple() {
    val result =
      compile(
        kotlin(
          "TestClass.kt",
          """
            package test

            import dev.zacsweers.lattice.annotations.Inject
            import java.util.concurrent.Callable

            class ExampleClass @Inject constructor(private val value: String) : Callable<String> {
              override fun call(): String = value
            }
          """
            .trimIndent(),
        ),
        debug = true,
      )
    result.assertCallableFactory("Hello, world!")
  }

  @Ignore("Not supported yet")
  @Test
  fun simpleGeneric() {
    val result =
      compile(
        kotlin(
          "TestClass.kt",
          """
            package test

            import dev.zacsweers.lattice.annotations.Inject
            import java.util.concurrent.Callable

            class ExampleClass<T> @Inject constructor(private val value: T) : Callable<T> {
              override fun call(): T = value
            }
          """
            .trimIndent(),
        ),
        debug = true,
      )
    result.assertCallableFactory("Hello, world!")
  }
}

fun JvmCompilationResult.assertCallableFactory(value: String) {
  val factory = ExampleClass.generatedFactoryClass()
  val callable = factory.invokeNewInstanceAs<Callable<String>>(value)
  assertThat(callable.call()).isEqualTo(value)
}

val JvmCompilationResult.ExampleClass: Class<*>
  get() {
    return classLoader.loadClass("test.ExampleClass")
  }

fun Class<*>.generatedFactoryClass(): Class<Factory<*>> {
  return classLoader.loadClass(name + "_Factory") as Class<Factory<*>>
}

fun Class<Factory<*>>.invokeNewInstance(vararg args: Any): Any {
  return declaredMethods.single { it.name == "newInstance" }.invoke(null, *args)
}

fun <T> Class<Factory<*>>.invokeNewInstanceAs(vararg args: Any): T {
  return declaredMethods.single { it.name == "newInstance" }.invoke(null, *args) as T
}

fun Class<Factory<*>>.invokeCreate(vararg args: Any): Factory<*> {
  return declaredMethods.single { it.name == "create" }.invoke(null, *args) as Factory<*>
}
