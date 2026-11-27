package com.leandro.petpass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.leandro.petpass.data.local.dao.PetDao
import com.leandro.petpass.data.local.dao.ExamDao
import com.leandro.petpass.data.local.dao.VaccineDao
import com.leandro.petpass.data.local.dao.WeightDao
import com.leandro.petpass.data.local.entity.ExamEntity
import com.leandro.petpass.data.local.entity.PetEntity
import com.leandro.petpass.data.local.entity.VaccineEntity
import com.leandro.petpass.data.local.entity.WeightEntity

@Database(
    entities = [PetEntity::class, VaccineEntity::class, WeightEntity::class, ExamEntity::class],
    version = 5,
    exportSchema = false
)
abstract class PetPassDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun examDao(): ExamDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun weightDao(): WeightDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS vaccines (
                        id TEXT NOT NULL PRIMARY KEY,
                        petId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        administeredAt TEXT NOT NULL,
                        nextDueDate TEXT,
                        veterinarian TEXT,
                        notes TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_vaccines_petId ON vaccines(petId)"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS weights (
                        id TEXT NOT NULL PRIMARY KEY,
                        petId TEXT NOT NULL,
                        weightKg REAL NOT NULL,
                        measuredAt TEXT NOT NULL,
                        veterinarian TEXT,
                        notes TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_weights_petId ON weights(petId)")
            }

        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS exams (
                        id TEXT NOT NULL PRIMARY KEY,
                        petId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        performedAt TEXT NOT NULL,
                        veterinarian TEXT,
                        result TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_exams_petId ON exams(petId)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pets ADD COLUMN rga TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
