package org.apache.daffodil.runtime2.generators

import org.apache.daffodil.grammar.primitives.OrderedSequence
import org.apache.daffodil.runtime2.Runtime2CodeGenerator

trait OrderedSequenceCodeGenerator {

  def orderedSequenceGenerateCode(g: OrderedSequence, cgState: CodeGeneratorState): Unit = {
    //
    // To lift this draconian restriction, we have to
    // generate code for each of the children, and combine them into a block
    //
    g.schemaDefinitionUnless(g.sequenceChildren.length == 1, "Only a single child of a sequence is supported.")
    val child1 = g.sequenceChildren(0)
    Runtime2CodeGenerator.generateCode(child1, cgState)
  }
}
