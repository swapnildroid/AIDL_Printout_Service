package com.example.aidlprintoutservice

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String
)

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE  // deletes orders when user is deleted
        )
    ],
    indices = [Index("userId")]  // important for performance
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val productName: String,
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PENDING  // Room stores this as a String by default
)

class Converters {
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String = status.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = OrderStatus.valueOf(value)
}

data class UserWithOrders(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val orders: List<Order>
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    // Must use @Transaction when fetching relations to ensure data consistency
    @Transaction
    @Query("SELECT * FROM users")
    suspend fun getAllUsersWithOrders(): List<UserWithOrders>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithOrders(userId: Long): UserWithOrders?

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Query("SELECT * FROM orders WHERE userId = :userId")
    suspend fun getOrdersForUser(userId: Long): List<Order>

    @Query("SELECT * FROM orders WHERE status = :status")
    suspend fun getOrdersByStatus(status: OrderStatus): List<Order>

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)
}

@Database(
    entities = [User::class, Order::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { INSTANCE = it }
            }
    }
}

class OrderRepository(private val db: AppDatabase) {

    suspend fun createUserWithOrders() {
        val userId = db.userDao().insertUser(
            User(name = "Alice", email = "alice@example.com")
        )

        db.orderDao().insertOrder(Order(userId = userId, productName = "Laptop", totalPrice = 999.99))
        db.orderDao().insertOrder(Order(userId = userId, productName = "Mouse", totalPrice = 29.99, status = OrderStatus.SHIPPED))
        db.orderDao().insertOrder(Order(userId = userId, productName = "Keyboard", totalPrice = 59.99, status = OrderStatus.DELIVERED))
    }

    suspend fun printUserOrders() {
        val userWithOrders = db.userDao().getUserWithOrders(1L)
        userWithOrders?.let {
            println("User: ${it.user.name}")
            it.orders.forEach { order ->
                println("  - ${order.productName}: \$${order.totalPrice} [${order.status}]")
            }
        }
    }

    suspend fun getShippedOrders(): List<Order> {
        return db.orderDao().getOrdersByStatus(OrderStatus.SHIPPED)
    }
}