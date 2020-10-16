/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.daffodil.runtime2

import org.apache.commons.io.FileUtils
import org.apache.daffodil.compiler.ProcessorFactory
import org.apache.daffodil.dsom.SchemaDefinitionError
import org.apache.daffodil.runtime2.generators.CodeGeneratorState
import org.apache.daffodil.util.Misc
import os.Pipe

class GeneratedCodeCompiler(pf: ProcessorFactory) {
  private var executableFile: os.Path = _
  private lazy val isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows")

  /**
   * Generates C code from the processor factory's compiled DFDL schema to
   * parse or unparse the given root element, compiles the generated C code,
   * and links it with the runtime2 library to build an executable file.
   */
  def compile(rootElementName: String): Unit = {
    val codeGeneratorState = new CodeGeneratorState()
    Runtime2CodeGenerator.generateCode(pf.sset.root.document, codeGeneratorState)

    val compiler = "cc"
    val location = Option(this.getClass.getProtectionDomain.getCodeSource) flatMap (x => Option(x.getLocation))
    val wd = if (os.exists(os.pwd/"daffodil-runtime2"))
      os.pwd
    else if (os.exists(os.pwd/os.up/"daffodil-runtime2"))
      os.pwd/os.up
    else if (location.isDefined)
      os.Path(FileUtils.toFile(location.get))/os.up/os.up
    else
      os.pwd
    val includeDir = if (os.exists(wd/"include"))
      wd/"include"
    else
      wd/"daffodil-runtime2"/"src"/"main"/"c"
    val libDir = if (os.exists(wd/"lib"))
      wd/"lib"
    else
      wd/"daffodil-runtime2"/"target"/"streams"/"compile"/"ccTargetMap"/"_global"/"streams"/"compile"/"sbtcc.Library"
    val generatedCodeHeader = codeGeneratorState.viewCodeHeader
    val generatedCodeFile = codeGeneratorState.viewCodeFile(rootElementName)
    val tempDir = os.temp.dir()
    val tempCodeHeader = tempDir/"generated_code.h"
    val tempCodeFile = tempDir/"generated_code.c"
    val depLib = if (isWindows) "-largp" else "-lpthread"
    val tempExe = tempDir/"daffodil"
    try {
      executableFile = null
      os.write(tempCodeHeader, generatedCodeHeader)
      os.write(tempCodeFile, generatedCodeFile)
      val result = os.proc(compiler, "-I", includeDir, tempCodeFile, "-L", libDir, "-lruntime2", "-lmxml", depLib, "-o", tempExe).call(cwd = tempDir, stderr = Pipe)
      if (!result.out.text.isEmpty || !result.err.text.isEmpty) {
        val sde = new SchemaDefinitionError(None, None, "Unexpected compiler output on stdout: %s on stderr: %s", result.out.text, result.err.text)
        pf.sset.warn(sde)
      }
      executableFile = tempExe
    } catch {
      case e: os.SubprocessException =>
        val sde = new SchemaDefinitionError(None, None, "Error compiling generated code: %s wd: %s", Misc.getSomeMessage(e).get, wd.toString)
        pf.sset.error(sde)
    }
  }

  /**
   * Gets a Runtime2DataProcessor to run the executable file.
   */
  def dataProcessor: Runtime2DataProcessor = {
    if (executableFile == null) throw new IllegalStateException("No executable file to run")
    new Runtime2DataProcessor(executableFile)
  }
}
