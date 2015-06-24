/* Copyright (c) 2015 Tresys Technology, LLC. All rights reserved.
 *
 * Developed by: Tresys Technology, LLC
 *               http://www.tresys.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal with
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 * 
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *  3. Neither the names of Tresys Technology, nor the names of its contributors
 *     may be used to endorse or promote products derived from this Software
 *     without specific prior written permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 * SOFTWARE.
 */

package edu.illinois.ncsa.daffodil.unparsing

import junit.framework.Assert._
import org.junit.Test
import scala.xml._
import edu.illinois.ncsa.daffodil.xml.XMLUtils
import edu.illinois.ncsa.daffodil.xml.XMLUtils._
import edu.illinois.ncsa.daffodil.util._
import edu.illinois.ncsa.daffodil.CLI.Util._
import edu.illinois.ncsa.daffodil.tdml.DFDLTestSuite
import junit.framework.Assert.assertEquals
import java.io.File
import edu.illinois.ncsa.daffodil.CLI.Util
import net.sf.expectit.Expect
import net.sf.expectit.matcher.Matchers.contains
import net.sf.expectit.matcher.Matchers.eof

class TestCLIunparsing {

  @Test def test_3525_CLI_Unparsing_SimpleUnparse_inFile() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input12.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("%s unparse -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3526_CLI_Unparsing_SimpleUnparse_inFile2() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input13.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("%s unparse -s %s --root e3 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("[1,2]"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3527_CLI_Unparsing_SimpleUnparse_stdin() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input14.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("cat %s | %s unparse -s %s --root e3", testInputFile, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[1,2]"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3528_CLI_Unparsing_SimpleUnparse_stdin2() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s unparse -s %s --root e1""", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3529_CLI_Unparsing_SimpleUnparse_stdin3() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s unparse -s %s --root e1 -""", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3584_CLI_Unparsing_SimpleUnparse_stdin4() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section06/entities/charClassEntities.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      val input = """'<tns:file xmlns:tns="http://www.example.org/example1/"><tns:header><tns:title>1</tns:title><tns:title>2</tns:title><tns:title>3</tns:title></tns:header><tns:record><tns:item>4</tns:item><tns:item>5</tns:item><tns:item>6</tns:item></tns:record></tns:file>'"""
      var cmd = String.format("echo %s | %s unparse -s %s --root file", input, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("1,2,3"))
      shell.expect(contains("4,5,6"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  /*
  // See DFDL-1347
  @Test def test_3574_CLI_Unparsing_SimpleUnparse_extVars() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section07/external_variables/external_variables.dfdl.xsd")
    val configFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section07/external_variables/daffodil_config_cli_test.xml"
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input15.txt"
    val (testSchemaFile, testConfigFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(configFile), Util.cmdConvert(inputFile)) else (schemaFile, configFile, inputFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("%s unparse -s %s -r row -D\"{http://example.com}var1=99\" -c %s %s", Util.binPath, testSchemaFile, testConfigFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("[0]"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3575_CLI_Unparsing_SimpleUnparse_extVars2() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section07/external_variables/external_variables.dfdl.xsd")
    val configFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section07/external_variables/daffodil_config_cli_test.xml"
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input16.txt"
    val (testSchemaFile, testConfigFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(configFile), Util.cmdConvert(inputFile)) else (schemaFile, configFile, inputFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("%s unparse -s %s -r row -c %s %s", Util.binPath, testSchemaFile, testConfigFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("[0]"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }
*/

  @Test def test_3582_CLI_Unparsing_SimpleUnparse_outFile() {
    val tmp_filename: String = (System.currentTimeMillis / 1000).toString()
    val file = new File(tmp_filename)
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input13.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)
    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s -r e3 -o %s %s", Util.binPath, testSchemaFile, tmp_filename, testInputFile)
      shell.sendLine(cmd)

      val catCmd = if (Util.isWindows) "type" else "cat"
      val openCmd = String.format("%s %s", catCmd, tmp_filename)

      shell.sendLine(openCmd)
      shell.expect(contains("[1,2]"))

      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
      assertTrue("Failed to remove temporary file: %s".format(file), file.delete)
    }
  }

  @Test def test_3581_CLI_Unparsing_SimpleUnparse_stOutDash() {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/input/input13.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)
    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s -r e3 -o - %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)

      shell.expect(contains("[1,2]"))

      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3580_CLI_Unparsing_SimpleUnparse_verboseMode() {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("", true)

    try {

      shell.sendLine(String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s -v unparse -s %s --root e1""", Util.binPath, testSchemaFile))
      shell.expect(contains("[info]"))

      shell.sendLine(String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s -vv unparse -s %s --root e1""", Util.binPath, testSchemaFile))
      shell.expect(contains("[compile]"))

      shell.sendLine(String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s -vvv unparse -s %s --root e1""", Util.binPath, testSchemaFile))
      shell.expect(contains("[debug]"))

      shell.sendLine(String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s -vvvv unparse -s %s --root e1""", Util.binPath, testSchemaFile))
      shell.expect(contains("[oolagdebug]"))
      shell.send("exit\n")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3579_CLI_Unparsing_negativeTest() {
    val shell = Util.start("", true)

    try {
      val cmd = String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s unparse""", Util.binPath)
      shell.sendLine(cmd)
      shell.expect(contains("One of --schema or --parser must be defined"))
      shell.send("exit\n")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3578_CLI_Unparsing_SimpleUnparse_defaultRoot() {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      var cmd = String.format("""echo '<tns:e1 xmlns:tns="http://example.com">Hello</tns:e1>' | %s unparse -s %s""", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  /*
  // See DFDL-1348
  @Test def test_3583_CLI_Unparsing_SimpleUnparse_rootPath() {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section06/entities/charClassEntities.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile

    val shell = Util.start("")

    try {
      val cmd = String.format("""echo '<tns:hcp2 xmlns:tns="http://www.example.org/example1/">12</tns:hcp2>' | %s unparse -s %s -r hcp2 -p /""", Util.binPath, testSchemaFile)

      shell.sendLine(cmd)
      shell.expect(contains("[12]"))
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }
*/

  /*
  // See DFDL-1346
  @Test def test_3576_CLI_Unparsing_validate() {

    val schemaFile = Util.daffodilPath("daffodil-cli/src/test/resources/edu/illinois/ncsa/daffodil/CLI/cli_schema.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile

    val shell = Util.start("", true)

    try {
      var cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate on """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[warning] Validation Error: validation_check: cvc-pattern-valid"))
      shell.expect(contains("[warning] Validation Error: element.validation_check failed"))

      cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[warning] Validation Error: validation_check: cvc-pattern-valid"))
      shell.expect(contains("[warning] Validation Error: element.validation_check failed"))

      cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate limited """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[warning] Validation Error: element.validation_check failed"))

      cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate off """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)

      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }
*/

  @Test def test_3577_CLI_Unparsing_traceMode() {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section06/namespaces/multi_base_15.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile
    val shell = Util.start("")

    try {
      val cmd = String.format("echo '<rabbitHole><nestSequence><nest>test</nest></nestSequence></rabbitHole>'| %s -t unparse -s %s", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("parser: <Element name='nest'><StringDelimitedUnparser/></Element>"))
      shell.expect(contains("parser: <Element name='rabbitHole'><ComplexType>...</ComplexType></Element>"))
      shell.expect(contains("test"))
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3662_CLI_Unparsing_badSchemaPath() {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/edu/illinois/ncsa/daffodil/section06/entities/doesnotexist.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile

    val shell = Util.start("", true)

    try {
      val cmd = String.format("echo '<tns:e1>Hello</tns:e1>' | %s unparse -s %s -r root", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Bad arguments for option 'schema'"))
      shell.expect(contains("Could not find file or resource"))
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

}
