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

import org.apache.daffodil.compiler.ProcessorFactory
import org.apache.daffodil.dsom.SchemaDefinitionError
import org.apache.daffodil.runtime2.generators.CodeGeneratorState
import org.apache.daffodil.util.Misc
import os.{ Path, Pipe }

class GeneratedCodeCompiler(pf: ProcessorFactory) {
  private var executableFile: Path = _

  // Original method now used only for hello world compilation unit testing
  def compile(codeGeneratorState: CodeGeneratorState): Unit = {
    val compiler = "cc"
    val code = codeGeneratorState.viewCode
    val tempDir = os.temp.dir()
    val tempCodeFile = tempDir / "code.c"
    val tempExe = tempDir / 'exe
    try {
      executableFile = null
      os.write(tempCodeFile, code)
      val result = os.proc(compiler, tempCodeFile, "-o", tempExe).call(cwd = tempDir, stderr = Pipe)
      if (!result.out.text.isEmpty || !result.err.text.isEmpty) {
        val sde = new SchemaDefinitionError(None, None, "Unexpected compiler output on stdout: %s on stderr: %s", result.out.text, result.err.text)
        pf.sset.warn(sde)
      }
      executableFile = tempExe
    } catch {
      case e: os.SubprocessException =>
        val sde = new SchemaDefinitionError(None, None, "Error compiling generated code: %s", Misc.getSomeMessage(e).get)
        pf.sset.error(sde)
    }
  }

  // New method, generates the C code needed to parse or unparse an input stream
  def compile(rootElementName: String, codeGeneratorState: CodeGeneratorState): Unit = {
    val compiler = "cc"
    val wd = if (os.exists(os.pwd / "daffodil-runtime2"))
      os.pwd
    else if (os.exists(os.pwd / os.up / "daffodil-runtime2"))
      os.pwd / os.up
    else if (Misc.classPath.nonEmpty)
      Path(Misc.classPath.head.getPath) / os.up / os.up
    else
      os.pwd
    val includeDir = if (os.exists(wd / "daffodil-runtime2"))
      wd / "daffodil-runtime2" / 'src / 'main / 'c
    else
      wd / 'include
    val libDir = if (os.exists(wd / "daffodil-runtime2"))
      wd / "daffodil-runtime2" / 'target / 'streams / 'compile / 'ccTargetMap / '_global / 'streams / 'compile / "sbtcc.Library"
    else
      wd / 'lib
    val generatedCodeHeader = codeGeneratorState.viewCodeHeader
    val generatedCodeFile = codeGeneratorState.viewCodeFile(rootElementName)
    val tempDir = os.temp.dir()
    val tempCodeHeader = tempDir / "generated_code.h"
    val tempCodeFile = tempDir / "generated_code.c"
    val tempExe = tempDir / 'daffodil
    try {
      executableFile = null
      os.write(tempCodeHeader, generatedCodeHeader)
      os.write(tempCodeFile, generatedCodeFile)
      val result = os.proc(compiler, "-I", includeDir, tempCodeFile, "-L", libDir, "-lruntime2", "-lmxml", "-lpthread", "-o", tempExe).call(cwd = tempDir, stderr = Pipe)
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

  def dataProcessor: Runtime2DataProcessor = new Runtime2DataProcessor(executableFile)
}
