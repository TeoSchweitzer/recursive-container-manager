package com.example.recursivecontainermanager.database

import android.content.Context
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.example.recursivecontainermanager.database.dao.ItemDAO
import com.example.recursivecontainermanager.database.dao.TokenDAO
import com.example.recursivecontainermanager.database.dao.TreeDAO
import com.example.recursivecontainermanager.database.dao.UserCredentialsDAO


@Database(
    entities = [Item::class, Tree::class, Token::class, UserCredentials::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DataBase : RoomDatabase() {
    abstract fun itemDao(): ItemDAO
    abstract fun treeDao(): TreeDAO
    abstract fun tokenDao(): TokenDAO
    abstract fun userCredentialsDao(): UserCredentialsDAO?

    companion object {
        @Volatile
        private var instance: DataBase? = null
        private val LOCK = Any()

        fun getDatabase(context: Context): DataBase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DataBase::class.java,"app_database")
                    .build()
                this.instance = instance

                instance
            }
        }
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: getDatabase(context).also{
                instance = it
            }
        }

    }
    /*
    companion object {
        const val DB_NAME = "Container.db"
        val context = ApplicationProvider.getApplicationContext<Context>()
        private lateinit var db: DataBase

        @Volatile
        private var instance: DataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also{
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DataBase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }*/
}