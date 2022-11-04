package app.personal.MVVM.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import app.personal.MVVM.Dao.localDao;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.Utls.Constants;

@Database(entities = {
        balanceEntity.class,
        debtEntity.class,
        expEntity.class,
        salaryEntity.class,
        budgetEntity.class},
        version = Constants.DB_VERSION)
public abstract class localDB extends RoomDatabase {

    private static localDB instance;

    public abstract localDao dao();

    public static synchronized localDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            localDB.class, Constants.dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
