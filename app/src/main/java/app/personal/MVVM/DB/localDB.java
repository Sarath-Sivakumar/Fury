package app.personal.MVVM.DB;

import android.content.Context;

import androidx.room.AutoMigration;
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
import app.personal.MVVM.Entity.userEntity;
import app.personal.Utls.Constants;

@Database(entities = {
        balanceEntity.class,
        debtEntity.class,
        expEntity.class,
        salaryEntity.class,
        budgetEntity.class,
        inHandBalEntity.class,
        userEntity.class},
        version = Constants.DB_LATEST_VERSION,
        autoMigrations = {
                @AutoMigration(from = 1, to = 2),
                @AutoMigration(from = 5, to = 6)
        })
public abstract class localDB extends RoomDatabase {

    private static localDB instance;

    public abstract localDao dao();

    public static synchronized localDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            localDB.class, Constants.dbName)
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,
                            MIGRATION_4_5,MIGRATION_6_7,MIGRATION_7_8,
                            MIGRATION_8_9)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'In_Hand_Bal_Table' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , 'balance' INTEGER)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Budget_Table' RENAME COLUMN 'percent' TO 'Amount'");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Budget_Table' ADD COLUMN 'bal' INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Salary_Table' ADD COLUMN 'creationDate' VARCHAR(15) DEFAULT 'Not Set'");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'User_Table' ('id' INTEGER NOT NULL, 'name' VARCHAR(25), 'imgUrl' VARCHAR(30), PRIMARY KEY('id'))");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Salary_Table' ADD COLUMN 'salMode' INTEGER NOT NULL DEFAULT 1");
        }
    };

    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Exp_Table' ADD COLUMN 'expMode' INTEGER NOT NULL DEFAULT 1");
        }
    };
}