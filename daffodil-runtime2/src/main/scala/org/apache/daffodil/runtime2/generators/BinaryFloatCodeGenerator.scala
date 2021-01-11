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

package org.apache.daffodil.runtime2.generators

import org.apache.daffodil.dsom.ElementBase
import org.apache.daffodil.schema.annotation.props.gen.ByteOrder

trait BinaryFloatCodeGenerator {

  def binaryFloatGenerateCode(e: ElementBase, lengthInBits: Int, cgState: CodeGeneratorState): Unit = {
    // For the time being this is a very limited back end.
    // So there are some restrictions to enforce.
    assert(lengthInBits == 32 || lengthInBits == 64)
    val byteOrder: ByteOrder = {
      e.schemaDefinitionUnless(e.byteOrderEv.isConstant, "Runtime dfdl:byteOrder expressions not supported.")
      val bo = e.byteOrderEv.constValue
      bo
    }

    // Use a signaling NAN to mark our field as uninitialized in case parsing
    // or unparsing fails to set the field.
    val nan = if (lengthInBits == 32) "SNANF" else "SNAN"
    val float = if (lengthInBits == 32) "float " else "double"
    val integer = if (lengthInBits == 32) "uint32_t" else "uint64_t"
    val conv = if (byteOrder eq ByteOrder.BigEndian) "be" else "le"
    val fieldName = e.namedQName.local

    val initStatement = s"    instance->$fieldName = $nan;"
    val parseStatement =
      s"""    if (!error_msg)
         |    {
         |        union
         |        {
         |            char     c_val[sizeof($float)];
         |            $float   f_val;
         |            $integer i_val;
         |        } buffer;
         |        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
         |        if (count < sizeof(buffer))
         |        {
         |            error_msg = eof_or_error_msg(pstate->stream);
         |        }
         |        buffer.i_val = ${conv}${lengthInBits}toh(buffer.i_val);
         |        instance->$fieldName = buffer.f_val;
         |    }""".stripMargin
    val unparseStatement =
      s"""    if (!error_msg)
         |    {
         |        union
         |        {
         |            char     c_val[sizeof($float)];
         |            $float   f_val;
         |            $integer i_val;
         |        } buffer;
         |        buffer.f_val = instance->$fieldName;
         |        buffer.i_val = hto${conv}${lengthInBits}(buffer.i_val);
         |        size_t count = fwrite(buffer.c_val, 1, sizeof(buffer), ustate->stream);
         |        if (count < sizeof(buffer))
         |        {
         |            error_msg = eof_or_error_msg(ustate->stream);
         |        }
         |    }""".stripMargin
    cgState.addSimpleTypeStatements(initStatement, parseStatement, unparseStatement)
  }
}
