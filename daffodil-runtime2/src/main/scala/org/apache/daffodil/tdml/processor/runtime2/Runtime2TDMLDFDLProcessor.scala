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

package org.apache.daffodil.tdml.processor.runtime2

import org.apache.daffodil.api._
import org.apache.daffodil.compiler.Compiler
import org.apache.daffodil.externalvars.Binding
import org.apache.daffodil.runtime2.GeneratedCodeCompiler
import org.apache.daffodil.runtime2.ParseResult
import org.apache.daffodil.runtime2.UnparseResult
import org.apache.daffodil.tdml.processor._
import org.apache.daffodil.xml.XMLUtils

import scala.xml.Node

final class TDMLDFDLProcessorFactory private(
  private var compiler: Compiler,
  private var checkAllTopLevel: Boolean,
  validateDFDLSchemasArg: Boolean)
  extends AbstractTDMLDFDLProcessorFactory {

  override def validateDFDLSchemas = validateDFDLSchemasArg

  override type R = TDMLDFDLProcessorFactory

  override def implementationName = "daffodil-runtime2"

  def this() = this(compiler = Compiler(validateDFDLSchemas = true),
    checkAllTopLevel = false,
    validateDFDLSchemasArg = true)

  private def copy(
    compiler: Compiler = compiler,
    checkAllTopLevel: Boolean = checkAllTopLevel,
    validateDFDLSchemas: Boolean = validateDFDLSchemas) =
    new TDMLDFDLProcessorFactory(compiler, checkAllTopLevel, validateDFDLSchemas)

  /**
   * Deprecated methods must be implemented. Some are just stubs though now.
   */
  @deprecated("Use withValidateDFDLSchemas.", "2.6.0")
  override def setValidateDFDLSchemas(bool: Boolean): Unit = {
    compiler = compiler.withValidateDFDLSchemas(bool)
  }

  override def withValidateDFDLSchemas(bool: Boolean): TDMLDFDLProcessorFactory = {
    copy(compiler = compiler.withValidateDFDLSchemas(bool))
  }

  @deprecated("Use withCheckAllTopLevel.", "2.6.0")
  override def setCheckAllTopLevel(checkAllTopLevel: Boolean): Unit = {
    compiler = compiler.withCheckAllTopLevel(checkAllTopLevel)
  }

  override def withCheckAllTopLevel(checkAllTopLevel: Boolean): TDMLDFDLProcessorFactory = {
    copy(compiler = compiler.withCheckAllTopLevel(checkAllTopLevel))
  }

  @deprecated("Use withTunables.", "2.6.0")
  override def setTunables(tunables: Map[String, String]): Unit =
    compiler = compiler.withTunables(tunables)

  override def withTunables(tunables: Map[String, String]): TDMLDFDLProcessorFactory =
    copy(compiler = compiler.withTunables(tunables))

  @deprecated("Use DaffodilTDMLDFDLProcessor.setExternalDFDLVariables.", "2.6.0")
  override def setExternalDFDLVariables(externalVarBindings: Seq[Binding]): Unit =
    compiler = compiler.withExternalDFDLVariablesImpl(externalVarBindings)

  override def withExternalDFDLVariables(externalVarBindings: Seq[Binding]): TDMLDFDLProcessorFactory =
    copy(compiler = compiler.withExternalDFDLVariablesImpl(externalVarBindings))

  @deprecated("Use arguments to getProcessor()", "2.6.0")
  override def setDistinguishedRootNode(name: String, namespace: String): Unit =
    compiler = compiler.withDistinguishedRootNode(name, namespace)

  // We're doing to replace this method with different code.
  // Return result is a TDML.CompileResult - so it's the result
  // of compiling the schema for the test.
  override def getProcessor(
    schemaSource: DaffodilSchemaSource,
    useSerializedProcessor: Boolean,
    optRootName: Option[String] = None,
    optRootNamespace: Option[String] = None): TDML.CompileResult = {
    val pf = compiler.compileSource(schemaSource, optRootName, optRootNamespace)
    val res = if (pf.isError) {
      Left(pf.getDiagnostics) // DFDL schema compilation diagnostics
    } else {
      // How can we move some of these calls to ProcessorFactory with tunable runtime = "runtime2"?
      val rootElementName = optRootName.getOrElse("FIXME")
      val generatedCodeCompiler = new GeneratedCodeCompiler(pf)
      generatedCodeCompiler.compile(rootElementName)
      val compileResult = if (pf.isError) {
        Left(pf.getDiagnostics) // C code compilation diagnostics
      } else {
        val dp = new Runtime2TDMLDFDLProcessor(generatedCodeCompiler)
        Right((pf.getDiagnostics, dp))
      }
      compileResult
    }
    res
  }

}

/**
 * Delegates all execution, error gathering, error access to the Runtime2DataProcessor object.
 * The responsibility of this class is just for TDML matching up. That is dealing with TDML
 * XML Infosets, feeding to the unparser, creating XML from the result created by the
 * Runtime2DataProcessor object. All the "real work" is done by generatedCodeCompiler.dataProcessor.
 */
class Runtime2TDMLDFDLProcessor(generatedCodeCompiler: GeneratedCodeCompiler) extends TDMLDFDLProcessor {

  override type R = Runtime2TDMLDFDLProcessor

  private val dataProcessor = generatedCodeCompiler.dataProcessor
  private var anyErrors: Boolean = false
  private var diagnostics: Seq[Diagnostic] = Nil

  @deprecated("Use withDebugging.", "2.6.0")
  override def setDebugging(b: Boolean) = ???
  override def withDebugging(b: Boolean): Runtime2TDMLDFDLProcessor = this

  @deprecated("Use withTracing.", "2.6.0")
  override def setTracing(bool: Boolean): Unit = ???
  override def withTracing(bool: Boolean): Runtime2TDMLDFDLProcessor = this

  @deprecated("Use withDebugger.", "2.6.0")
  override def setDebugger(db: AnyRef): Unit = ???
  override def withDebugger(db: AnyRef): Runtime2TDMLDFDLProcessor = this

  @deprecated("Use withValidationMode.", "2.6.0")
  override def setValidationMode(validationMode: ValidationMode.Type): Unit = ???
  override def withValidationMode(validationMode: ValidationMode.Type): Runtime2TDMLDFDLProcessor = this

  @deprecated("Use withExternalDFDLVariables.", "2.6.0")
  override def setExternalDFDLVariables(externalVarBindings: Seq[Binding]): Unit = ???
  override def withExternalDFDLVariables(externalVarBindings: Seq[Binding]): Runtime2TDMLDFDLProcessor = this

  // Actually run the C code and save any errors to be returned here
  override def isError: Boolean = anyErrors
  override def getDiagnostics: Seq[Diagnostic] = diagnostics

  // This part will change a lot (it will execute C code instead).
  // Whatever the parse produces needs to be converted into XML for comparison.
  // We'll need a way to convert, say, a C struct to XML, and XML to C struct.
  // The C code will need a bunch of toXML methods so it can produce output
  // for comparison.
  override def parse(is: java.io.InputStream, lengthLimitInBits: Long): TDMLParseResult = {
    // We will run the generated and compiled C code, collect and save any errors
    // and diagnostics to be returned in isError and getDiagnostics, and build an
    // infoset.  Our context here is TDML-related, so we need to move that functionality
    // to something generic that we call from here, you're saying.  I got it.  So we
    // put that more generic "RunGeneratedCode" functionality in runtime2 too... in
    // another package, not parser, maybe runtime2 itself?  Should we call this generic
    // class RuntimeState?  Yes, get rid of PState.

    // Call the C program via subprocess.  Have it parse the input stream and
    // return the XML result on its standard output.  Errors and diagnostics
    // can come back via standard error.
    // Or: define a result object structure (XML-based probably) that includes
    // both infoset and errors/diagnostics.  Segfaults probably still will generate
    // something on stderr.  What should diags/errors look like?  text lines, XML?
    // difficult to expect XML, but suddenly read segfault output or other output.
    // Start diags with recognizable prefix that tells us our runtime code made them
    // lines without that prefix are captured as generic errors

    // TODO: pass lengthLimitInBits to the C program to tell it how big the data is
    val pr = dataProcessor.parse(is)
    anyErrors = pr.isError
    diagnostics = pr.getDiagnostics
    new Runtime2TDMLParseResult(pr)
  }

  override def unparse(infosetXML: scala.xml.Node, outStream: java.io.OutputStream): TDMLUnparseResult = {
    val tempDir = null
    val tempInputFile = XMLUtils.convertNodeToTempFile(infosetXML, tempDir)
    val inStream = os.read.inputStream(os.Path(tempInputFile))
    val upr = dataProcessor.unparse(inStream, outStream)
    anyErrors = upr.isError
    diagnostics = upr.getDiagnostics
    new Runtime2TDMLUnparseResult(upr)
  }

  def unparse(parseResult: TDMLParseResult, outStream: java.io.OutputStream): TDMLUnparseResult = {
    unparse(parseResult.getResult, outStream)
  }
}

final class Runtime2TDMLParseResult(pr: ParseResult) extends TDMLParseResult {
  override def addDiagnostic(failure: Diagnostic): Unit = pr.addDiagnostic(failure)

  override def getResult: Node = pr.infosetAsXML

  override def currentLocation: DataLocation = pr.currentLocation

  override def isValidationError: Boolean = pr.isValidationError

  override def isProcessingError: Boolean = pr.isProcessingError

  override def getDiagnostics: Seq[Diagnostic] = pr.getDiagnostics
}

final class Runtime2TDMLUnparseResult(upr: UnparseResult) extends TDMLUnparseResult {
  override def bitPos0b: Long = upr.finalBitPos0b

  override def finalBitPos0b: Long = upr.finalBitPos0b

  override def isScannable: Boolean = upr.isScannable

  override def encodingName: String = upr.encodingName

  override def isValidationError: Boolean = upr.isValidationError

  override def isProcessingError: Boolean = upr.isProcessingError

  override def getDiagnostics: Seq[Diagnostic] = upr.getDiagnostics
}
