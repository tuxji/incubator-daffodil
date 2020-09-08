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

import org.apache.daffodil.api.DFDL
import org.apache.daffodil.dpath.NodeInfo
import org.apache.daffodil.dpath.NodeInfo.PrimType
import org.apache.daffodil.dsom.ElementBase
import org.apache.daffodil.exceptions.ThrowsSDE

import scala.collection.mutable

trait ParserGenerator {
  // TBD: if the code-generator state builds up content by side-effect, there's no
  // reason to be returning it. It should return Unit.
  def generateCode(state: CodeGeneratorState): Unit
}

/**
 * Builds up the state of generated code.
 */
class CodeGeneratorState(private val code: String) extends DFDL.CodeGeneratorState {
  private val structs = mutable.Stack[ComplexCGState]()
  private val prototypes = mutable.ArrayBuffer[String]()
  private val erds = mutable.ArrayBuffer[String]()
  private val finalStructs = mutable.ArrayBuffer[String]()
  private val finalImplementation = mutable.ArrayBuffer[String]()

  def this() = this(null)

  def addImplementation(context: ElementBase): Unit = {
    val C = context.namedQName.local
    val initStatements = structs.top.initStatements.mkString("\n")
    val parserStatements = structs.top.parserStatements.mkString("\n")
    val unparserStatements = structs.top.unparserStatements.mkString("\n")
    val prototypeFunctions =
      s"""static void        ${C}_initSelf($C *instance);
         |static const char *${C}_parseSelf($C *instance, const PState *pstate);
         |static const char *${C}_unparseSelf(const $C *instance, const UState *ustate);""".stripMargin
    prototypes += prototypeFunctions
    val functions =
      s"""static void
         |${C}_initSelf($C *instance)
         |{
         |$initStatements
         |}
         |
         |static const char *
         |${C}_parseSelf($C *instance, const PState *pstate)
         |{
         |    const char *error_msg = NULL;
         |$parserStatements
         |    return error_msg;
         |}
         |
         |static const char *
         |${C}_unparseSelf(const $C *instance, const UState *ustate)
         |{
         |    const char *error_msg = NULL;
         |$unparserStatements
         |    return error_msg;
         |}
         |""".stripMargin
    finalImplementation += functions
  }

  private def defineQNameInit(context: ElementBase): String = {
    val qname = context.namedQName.toQNameString
    val xmlns = if (context.namedQName.prefix.isDefined) s"xmlns:${context.namedQName.prefix.get}" else "xmlns"
    val ns = context.namedQName.namespace.toStringOrNullIfNoNS
    val qnameInit = if (ns == null)
      s"""    {"$qname"},          // namedQName.name"""
    else
      s"""    {
         |        "$qname",              // namedQName.name
         |        "$xmlns",           // namedQName.xmlns
         |        "$ns", // namedQName.ns
         |    },""".stripMargin
    qnameInit
  }

  def addComplexTypeERD(context: ElementBase): Unit = {
    val C = context.namedQName.local
    val count = structs.top.declarations.length
    val offsetComputations = structs.top.offsetComputations.mkString(",\n")
    val erdComputations = structs.top.erdComputations.mkString(",\n")
    val qnameInit = defineQNameInit(context)
    val complexERD =
      s"""static const $C ${C}_compute_ERD_offsets;
         |
         |static const ptrdiff_t ${C}_offsets[$count] = {
         |$offsetComputations
         |};
         |
         |static const ERD *${C}_childrenERDs[$count] = {
         |$erdComputations
         |};
         |
         |static const ERD ${C}_ERD = {
         |$qnameInit
         |    COMPLEX,                         // typeCode
         |    $count,                               // numChildren
         |    ${C}_offsets,                      // offsets
         |    ${C}_childrenERDs,                 // childrenERDs
         |    (ERDInitSelf)&${C}_initSelf,       // initSelf
         |    (ERDParseSelf)&${C}_parseSelf,     // parseSelf
         |    (ERDUnparseSelf)&${C}_unparseSelf, // unparseSelf
         |};
         |""".stripMargin
    erds += complexERD
  }

  def addStruct(context: ElementBase): Unit = {
    val C = context.namedQName.local
    val declarations = structs.top.declarations.mkString("\n")
    val struct =
      s"""typedef struct $C
         |{
         |    InfosetBase _base;
         |$declarations
         |} $C;
         |""".stripMargin
    finalStructs += struct
    val initStatement = s"    instance->_base.erd = &${C}_ERD;"
    structs.top.initStatements += initStatement
  }

  def addSimpleTypeStatements(initStatement: String, parseStatement: String, unparseStatement: String): Unit = {
    structs.top.initStatements += initStatement
    structs.top.parserStatements += parseStatement
    structs.top.unparserStatements += unparseStatement
  }

  def addComplexTypeStatements(child: ElementBase): Unit = {
    val C = child.namedQName.local
    val e = child.name
    val initStatement = s"    ${C}_initSelf(&instance->$e);"
    val parseStatement =
      s"""    if (error_msg == NULL)
         |    {
         |        error_msg = ${C}_parseSelf(&instance->$e, pstate);
         |    }""".stripMargin
    val unparseStatement =
      s"""    if (error_msg == NULL)
         |    {
         |        error_msg = ${C}_unparseSelf(&instance->$e, ustate);
         |    }""".stripMargin
    structs.top.initStatements += initStatement
    structs.top.parserStatements += parseStatement
    structs.top.unparserStatements += unparseStatement
  }

  def pushComplexElement(context: ElementBase): Unit = {
    val C = context.namedQName.local
    structs.push(new ComplexCGState(C))
  }

  def popComplexElement(context: ElementBase): Unit = {
    structs.pop()
  }

  def addSimpleTypeERD(context: ElementBase): Unit = {
    val e = context.namedQName.local
    val qnameInit = defineQNameInit(context)
    val typeCode = context.optPrimType.get match {
      case PrimType.Int => "PRIMITIVE_INT32"
      case PrimType.String => "PRIMITIVE_STRING"
      case p: PrimType => context.SDE("PrimType %s not supported yet.", p.toString)
    }
    val erd =
      s"""static const ERD ${e}_ERD = {
         |$qnameInit
         |    $typeCode, // typeCode
         |    0,               // numChildren
         |    NULL,            // offsets
         |    NULL,            // childrenERDs
         |    NULL,            // initSelf
         |    NULL,            // parseSelf
         |    NULL,            // unparseSelf
         |};
         |""".stripMargin
    erds += erd
    addComputations(context)
  }

  def addComputations(child: ElementBase): Unit = {
    val C = structs.top.C
    val e = child.namedQName.local
    val offsetComputation = s"    (char *)&${C}_compute_ERD_offsets.$e - (char *)&${C}_compute_ERD_offsets"
    val erdComputation = s"    &${e}_ERD"
    structs.top.offsetComputations += offsetComputation
    structs.top.erdComputations += erdComputation
  }

  def addFieldDeclaration(context: ThrowsSDE, child: ElementBase): Unit = {
    val definition = if (child.isSimpleType) {
      import NodeInfo.PrimType
      child.optPrimType.get match {
        case PrimType.Long => "int64_t    "
        case PrimType.Int => "int32_t    "
        case x => context.SDE("Unsupported primitive type: " + x)
      }
    } else {
      child.namedQName.local + "         "
    }
    structs.top.declarations += s"    $definition ${child.name};"
  }

  def viewCode: String = code

  def viewCodeHeader: String = {
    val structs = finalStructs.mkString("\n")
    val header =
      s"""#ifndef GENERATED_CODE_H
         |#define GENERATED_CODE_H
         |
         |#include "common_runtime.h" // for InfosetBase
         |#include <stdint.h>         // for int32_t
         |
         |// Return the root of an infoset to be used for parsing or unparsing
         |
         |extern InfosetBase *rootInfoset();
         |
         |// Define some infoset structures
         |
         |$structs
         |#endif // GENERATED_CODE_H
         |""".stripMargin
    header
  }

  def viewCodeFile(rootElementName: String): String = {
    val prototypes = this.prototypes.mkString("\n")
    val erds = this.erds.mkString("\n")
    val finalImplementation = this.finalImplementation.mkString("\n")
    val code =
      s"""#include "generated_code.h"
         |#include <endian.h> // for be32toh, htobe32
         |#include <errno.h>  // for errno
         |#include <stddef.h> // for ptrdiff_t
         |#include <stdio.h>  // for NULL, fread, fwrite, size_t, feof, ferror, FILE
         |#include <string.h> // for strerror
         |
         |// Prototypes needed for compilation
         |
         |$prototypes
         |
         |// Metadata singletons
         |
         |$erds
         |// Return the root of an infoset to be used for parsing or unparsing
         |
         |InfosetBase *
         |rootInfoset()
         |{
         |    static $rootElementName    instance;
         |    InfosetBase *root = &instance._base;
         |    ${rootElementName}_ERD.initSelf(root);
         |    return root;
         |}
         |
         |// Methods to initialize, parse, and unparse infoset nodes
         |
         |static const char *
         |eof_or_error_msg(FILE *stream)
         |{
         |    if (feof(stream))
         |    {
         |        static const char *error_msg = "Got EOF while expecting more input";
         |        return error_msg;
         |    }
         |    else if (ferror(stream))
         |    {
         |        return strerror(errno);
         |    }
         |    else
         |    {
         |        return NULL;
         |    }
         |}
         |
         |$finalImplementation
         |""".stripMargin
    code
  }
}

class ComplexCGState(val C: String) {
  val declarations = mutable.ArrayBuffer[String]()
  val offsetComputations = mutable.ArrayBuffer[String]()
  val erdComputations = mutable.ArrayBuffer[String]()
  val initStatements = mutable.ArrayBuffer[String]()
  val parserStatements = mutable.ArrayBuffer[String]()
  val unparserStatements = mutable.ArrayBuffer[String]()
}
