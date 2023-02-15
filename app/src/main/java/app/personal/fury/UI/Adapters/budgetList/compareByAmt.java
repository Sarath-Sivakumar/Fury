package app.personal.fury.UI.Adapters.budgetList;

import java.util.Comparator;

import app.personal.MVVM.Entity.expEntity;

public class compareByAmt implements Comparator<expEntity> {
    @Override
    public int compare(expEntity a, expEntity b) {
        return b.getExpenseAmt()-a.getExpenseAmt();
    }
}
