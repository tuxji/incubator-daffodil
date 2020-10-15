package org.apache.daffodil.runtime2.generators

import org.apache.daffodil.grammar.SeqComp
import org.apache.daffodil.runtime2.Runtime2CodeGenerator

trait SeqCompCodeGenerator {

  def seqCompGenerateCode(g: SeqComp, state: CodeGeneratorState): Unit = {
    for (elem <- g.children) {
      Runtime2CodeGenerator.generateCode(elem, state)
    }
  }
}
