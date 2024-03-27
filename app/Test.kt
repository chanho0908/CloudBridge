import java.sql.DriverManager.println


data class Triple(val first:String, val second:String, val third: String)

fun main(){
    /* Triple 예제 */
    val triple = Triple("현대", "그랜저", "그랜저 IG")

    println(triple.first)
    println(triple.second)
    println(triple.third)
}


