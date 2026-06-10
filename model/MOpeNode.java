package model;

import java.io.Serializable;

// Node of a mOPE tree
public class MOpeNode implements Serializable {
    private static final long serialVersionUID = 1L;

    public int plaintext;   // real value
    public long opeValue;   // OPE encrypt value
    
    public MOpeNode left;
    public MOpeNode right;

    public MOpeNode(int plaintext, long opeValue) {
        this.plaintext = plaintext;
        this.opeValue = opeValue;
        this.left = null;
        this.right = null;
    }
}