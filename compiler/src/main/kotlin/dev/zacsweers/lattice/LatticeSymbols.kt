/*
 * Copyright (C) 2024 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.lattice

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.declarations.createEmptyExternalPackageFragment
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal class LatticeSymbols(
  private val moduleFragment: IrModuleFragment,
  pluginContext: IrPluginContext,
) {

  private val irFactory: IrFactory = pluginContext.irFactory
  private val latticeRuntime: IrPackageFragment by lazy { createPackage("dev.zacsweers.lattice") }
  private val latticeRuntimeInternal: IrPackageFragment by lazy {
    createPackage("dev.zacsweers.lattice.internal")
  }
  private val latticeAnnotations: IrPackageFragment by lazy {
    createPackage("dev.zacsweers.lattice.annotations")
  }
  private val stdlib: IrPackageFragment by lazy { createPackage("kotlin") }
  private val stdlibJvm: IrPackageFragment by lazy { createPackage("kotlin.jvm") }

  val latticeInject: IrClassSymbol by lazy {
    pluginContext.referenceClass(
      ClassId(latticeAnnotations.packageFqName, Name.identifier("Inject"))
    )!!
  }

  val latticeProvider: IrClassSymbol by lazy {
    pluginContext.referenceClass(
      ClassId(latticeRuntime.packageFqName, Name.identifier("Provider"))
    )!!
  }
  val providerValuePropertyGetter: IrSimpleFunctionSymbol by lazy {
    latticeProvider.getPropertyGetter("value")!!
  }
  val providerValueProperty: IrPropertySymbol by lazy {
    providerValuePropertyGetter.owner.correspondingPropertySymbol!!
  }

  val latticeFactory: IrClassSymbol by lazy {
    pluginContext.referenceClass(
      ClassId(latticeRuntimeInternal.packageFqName, Name.identifier("Factory"))
    )!!
  }

  val stdlibLazy: IrClassSymbol by lazy {
    pluginContext.referenceClass(ClassId(stdlib.packageFqName, Name.identifier("Lazy")))!!
  }

  val jvmStatic: IrClassSymbol by lazy {
    pluginContext.referenceClass(ClassId(stdlibJvm.packageFqName, Name.identifier("JvmStatic")))!!
  }

  val injectAnnotations = setOf(latticeInject.owner.classIdOrFail)
  // TODO
  val assistedAnnotations = setOf<ClassId>()
  val providerTypes = setOf(latticeProvider.owner.classIdOrFail)
  val lazyTypes = setOf(stdlibLazy.owner.classIdOrFail)

  protected fun createPackage(packageName: String): IrPackageFragment =
    createEmptyExternalPackageFragment(moduleFragment.descriptor, FqName(packageName))
}
