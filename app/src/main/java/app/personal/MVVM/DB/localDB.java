package app.personal.MVVM.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import app.personal.MVVM.Dao.localDao;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.Utls.Constants;

@Database(entities = {
        balanceEntity.class,
        debtEntity.class,
        expEntity.class,
        salaryEntity.class,
        budgetEntity.class,
        inHandBalEntity.class},
        version = Constants.DB_LATEST_VERSION)
public abstract class localDB extends RoomDatabase {

    private static localDB instance;

    public abstract localDao dao();

    public static synchronized localDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            localDB.class, Constants.dbName)
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'In_Hand_Bal_Table' ('id' INTEGER, 'balance' INTEGER, PRIMARY KEY('id'))");
        }
    };

}
