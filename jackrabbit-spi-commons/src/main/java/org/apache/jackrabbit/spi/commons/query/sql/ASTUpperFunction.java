/* Generated By:JJTree: Do not edit this line. ASTUpperFunction.java */

package org.apache.jackrabbit.spi.commons.query.sql;

public class ASTUpperFunction extends SimpleNode {
  public ASTUpperFunction(int id) {
    super(id);
  }

  public ASTUpperFunction(JCRSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JCRSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}