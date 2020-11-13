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

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

import dev.dirs.ProjectDirectories
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
   * Creates or updates a Daffodil cache directory for holding C source files
   * and native machine binary files.
   */
  def clearDaffodilCache(): os.Path = {
    // Look up our cache directory's location on Unix, MacOS, or Windows
    val directories = ProjectDirectories.from("org", "Apache Software Foundation", "Daffodil")
    val daffodilCache = Path.of(directories.cacheDir)

    // Delete code subdirectory to avoid any mismatched versions
    val codeDir = daffodilCache.resolve("c")
    if (Files.exists(codeDir))
      FileUtils.deleteDirectory(codeDir.toFile)

    // Copy C source files from our resources into code subdirectory
    val codeResource = Path.of(Misc.getRequiredResource("c"))
    FileUtils.copyDirectory(codeResource.toFile, codeDir.toFile)

    // Our cache directory is now ready for use
    os.Path(daffodilCache)
  }

  /**
   * Searches for any available C compiler on the system.  Tries to find the
   * compiler given by `CC` if `CC` exists in the environment, then tries to
   * find any compiler from the following list:
   *
   *   - zig cc
   *   - gcc
   *   - clang
   *   - cc
   *
   * Returns the first compiler found and splits that compiler's string into a
   * sequence of strings in case the compiler's string has subcommands or options
   * separated by spaces.  Returns the empty sequence if no compiler could be
   * found in the user's PATH.
   */
  lazy val pickCompiler: Seq[String] = {
    val ccEnv = System.getenv("CC")
    val compilers = Seq(ccEnv, "zig cc", "gcc", "clang", "cc")
    val path = System.getenv("PATH").split(File.pathSeparatorChar)
    def inPath(compiler: String): Boolean = {
      (compiler != null) && {
        val exec = compiler.takeWhile(_ != ' ')
        val exec2 = exec + ".exe"
        path.exists(dir => Files.isExecutable(Path.of(dir, exec))
          || (isWindows && Files.isExecutable(Path.of(dir, exec2))))
      }
    }
    val compiler = compilers.find(inPath)
    if (compiler.isDefined)
      compiler.get.split(' ').toSeq
    else
      Seq.empty[String]
  }

  /**
   * Generates C code from the processor factory's compiled DFDL schema to
   * parse or unparse the given root element, compiles the generated C code
   * along with C source files from our resources, and builds an executable file.
   */
  def compile(rootElementName: String): Unit = {
    // Set up some paths we'll need
    executableFile = null
    val wd = clearDaffodilCache()
    val codeDir = wd/"c"
    val generatedCodeHeader = codeDir/"generated_code.h"
    val generatedCodeFile = codeDir/"generated_code.c"
    val exe = codeDir/"daffodil"

    try {
      // Generate C code from the compiled DFDL schema
      val codeGeneratorState = new CodeGeneratorState()
      Runtime2CodeGenerator.generateCode(pf.sset.root.document, codeGeneratorState)
      val codeHeaderText = codeGeneratorState.generateCodeHeader
      val codeFileText = codeGeneratorState.generateCodeFile(rootElementName)
      os.write(generatedCodeHeader, codeHeaderText)
      os.write(generatedCodeFile, codeFileText)

      // Assemble the compiler's command line arguments
      val compiler = pickCompiler
      val files = os.list(codeDir).filter(_.ext == "c")
      val libs = Seq("-lmxml", if (isWindows) "-largp" else "-lpthread")

      // Call the compiler if it was found
      if (compiler.nonEmpty) {
        val result = os.proc(compiler, "-I", codeDir, files, libs, "-o", exe).call(cwd = wd, stderr = Pipe)

        // Print any compiler output as a warning
        if (result.out.text.nonEmpty || result.err.text.nonEmpty) {
          val sde = new SchemaDefinitionError(None, None, "Unexpected compiler output on stdout: %s on stderr: %s",
            result.out.text, result.err.text)
          pf.sset.warn(sde)
        }
      }

      // Save the executable's path if it was built
      if (os.exists(exe)) {
        executableFile = exe
      } else {
        val sde = new SchemaDefinitionError(None, None, "No executable was built: %s not found",
          exe.toString)
        pf.sset.error(sde)
      }
    } catch {
      // Handle any compiler errors
      case e: os.SubprocessException =>
        val sde = new SchemaDefinitionError(None, None, "Error compiling generated code: %s wd: %s",
          Misc.getSomeMessage(e).get, wd.toString)
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
