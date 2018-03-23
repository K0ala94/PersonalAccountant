package mullerge.personalaccountent.expense;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpenseType {

    public static final List<String> TYPES = new ArrayList<>();
    public static final List<String> SUM_TYPES = new ArrayList<>();

    static{
        TYPES.add("FOOD");
        TYPES.add("GROCERY");
        TYPES.add("GAS");
        TYPES.add("RECR.");
        TYPES.add("CLOTHS");
        TYPES.add("BILLS");
        TYPES.add("HOME");

        SUM_TYPES.add("ALL");
        SUM_TYPES.addAll(TYPES);
    }




}
