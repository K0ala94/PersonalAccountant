package mullerge.personalaccountent.expense;


import java.util.HashSet;
import java.util.Set;

public class ExpenseType {

    public static final Set<String> TYPES = new HashSet<>();

    static{
        TYPES.add("FOOD");
        TYPES.add("GAS");
        TYPES.add("RECREATION");
        TYPES.add("CLOTHING");
        TYPES.add("BILLS");
    }




}
