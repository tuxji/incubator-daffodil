package org.apache.daffodil.runtime2

import org.apache.daffodil.compiler.ProcessorFactory
import org.apache.daffodil.dsom.SchemaDefinitionError
import org.apache.daffodil.runtime2.generators.CodeGeneratorState
import org.apache.daffodil.util.Misc
import os.{ Path, Pipe }

class GeneratedCodeCompiler(pf: ProcessorFactory) {
  private var executableFile: Path = _

  def compile(codeGeneratorState: CodeGeneratorState) = {
    val generatedCode = codeGeneratorState.viewCode
    val tempDir = os.temp.dir()
    val tempFile = tempDir / "hello.c"
    val tempExe = tempDir / "hello"
    executableFile = null
    try {
      os.write(tempFile, generatedCode)
      val result = os.proc("gcc", tempFile, "-o", tempExe).call(stderr = Pipe)
      if (!result.out.text.isEmpty || !result.err.text.isEmpty) {
        pf.sset.SDW(null, "Captured warning messages\n" + result.out.text + result.err.text)
      }
      executableFile = tempExe
    } catch {
      case e: os.SubprocessException => {
        val sde = new SchemaDefinitionError(None, None, Misc.getSomeMessage(e).get)
        pf.sset.error(sde)
      }
    }
  }

  def dataProcessor: Runtime2DataProcessor = ???
}
