package MiniJava.codeGenerator;
import MiniJava.codeGenerator.typeAddress.Direct;
import MiniJavacodeGenerator.typeAddress.TypeAddress;

/**
 * Created by mohammad hosein on 6/28/2015.
 */
@@ -11,25 +14,21 @@ public class Address {

    public Address(int num, varType varType, TypeAddress Type) {
        this.num = num;

        this.varType = varType;
        this.Type = Type;
    }

    public Address(int num, varType varType) {
        this.num = num;

        this.varType = varType;
        this.Type = new Direct();
    }

    public String toString() {
        if (Type == null) {
            return num + "";





        }

        return Type.toString(num);
    }
}