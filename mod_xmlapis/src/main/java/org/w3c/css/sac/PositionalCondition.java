/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: PositionalCondition.java 477010 2006-11-20 02:54:38Z mrglavas $
 */
package org.w3c.css.sac;

/**
 * @version $Revision: 477010 $
 * @author  Philippe Le Hegaret
 * @see org.w3c.css.sac.Condition#SAC_POSITIONAL_CONDITION
 */
public interface PositionalCondition extends Condition {

    /**
     * Returns the position in the tree.
     * <p>A negative value means from the end of the child node list.
     * <p>The child node list begins at 0.
     */    
    public int getPosition();

    /**
     * <code>true</code> if the child node list only shows nodes of the same
     * type of the selector (only elements, only PIS, ...)
     */
    public boolean getTypeNode();

    /**
     * <code>true</code> if the node should have the same node type (for
     *  element, same namespaceURI and same localName).
     */
    public boolean getType();
}