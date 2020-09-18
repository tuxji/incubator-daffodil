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

class ElementParserGenerator(context: ElementBase, contentParserGenerator: ParserGenerator)
  extends ParserGenerator {

  override def generateCode(cgState: CodeGeneratorState): Unit = {

    if (context.isSimpleType) {
      cgState.addSimpleTypeERD(context) // ERD static initializer
      contentParserGenerator.generateCode(cgState) // initSelf, parseSelf, unparseSelf
    } else {
      cgState.pushComplexElement(context)
      context.elementChildren.foreach { child =>
        if (!child.isSimpleType) {
          cgState.addComplexTypeStatements(child) // recursive calls to parse, unparse, init
          cgState.addComputations(child) // offset, ERD computations
        }
        cgState.addFieldDeclaration(context, child) // struct member for child
        child.enclosedElement.generateCode(cgState) // generate children too
      }
      cgState.addStruct(context) // struct definition
      cgState.addImplementation(context) // initSelf, parseSelf, unparseSelf
      cgState.addComplexTypeERD(context) // ERD static initializer
      cgState.popComplexElement(context)
    }
  }
}
