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

import java.io.ByteArrayInputStream

import org.apache.daffodil.compiler.Compiler
import org.apache.daffodil.infoset.DIComplex
import org.apache.daffodil.infoset.TestInfoset
import org.apache.daffodil.util.Misc
import org.apache.daffodil.util.SchemaUtils
import org.junit.Test

// Now that we can run TDML tests with runtime2, this test's remaining
// value is for debugging of runtime2 components.
class TestGeneratedCodeCompiler {

  @Test
  def scratchTest(): Unit = {
    val generatedCodeCompiler = new GeneratedCodeCompiler(null)
    val daffodilCache = generatedCodeCompiler.clearDaffodilCache()
    assert(os.exists(daffodilCache))
    assert(os.exists(daffodilCache/"c"))
  }

  @Test
  def compileRunParseInt32(): Unit = {
    // Compile a DFDL schema to parse int32 numbers
    val testSchema = SchemaUtils.dfdlTestSchema(
        <xs:include schemaLocation="org/apache/daffodil/xsd/DFDLGeneralFormat.dfdl.xsd"/>,
        <dfdl:format representation="binary" ref="GeneralFormat"/>,
      <xs:element name="c1">
        <xs:complexType>
          <xs:sequence>
            <xs:element name="e1" type="xs:int"/>
            <xs:element name="c2">
              <xs:complexType>
                <xs:sequence>
                  <xs:element name="e2" type="xs:int"/>
                  <xs:element name="e3" type="xs:int"/>
                </xs:sequence>
              </xs:complexType>
            </xs:element>
          </xs:sequence>
        </xs:complexType>
      </xs:element>)
    val schemaCompiler = Compiler()
    val pf = schemaCompiler.compileNode(testSchema)
    assert(!pf.isError, pf.getDiagnostics.map(_.getMessage()).mkString("\n"))
    // Generate C code from the DFDL schema
    val generatedCodeCompiler = new GeneratedCodeCompiler(pf)
    val rootElementName = "c1"
    generatedCodeCompiler.compile(rootElementName)
    assert(!pf.isError, pf.getDiagnostics.map(_.getMessage()).mkString("\n"))
    // Run the executable to parse int32 numbers
    val dp = generatedCodeCompiler.dataProcessor
    val b = Misc.hex2Bytes("000000010000000200000003")
    val input = new ByteArrayInputStream(b)
    val pr = dp.parse(input)
    assert(!pr.isError && pf.getDiagnostics.isEmpty, pr.getDiagnostics.map(_.getMessage()).mkString("\n"))
    // Create an internal Daffodil infoset from the XML file
    val (infoset: DIComplex, _, tunables) = TestInfoset.testInfoset(testSchema, pr.infosetAsXML)
    assert(infoset.hasVisibleChildren)
    assert(infoset.erd.name == "c1")
  }

}
