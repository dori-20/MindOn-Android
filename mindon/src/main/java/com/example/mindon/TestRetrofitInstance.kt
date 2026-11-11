import com.example.mindon.TestApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TestRetrofitInstance {

    private const val BASE_URL = "http://mindon-test.great-site.net/"

    private val gson = GsonBuilder()
        .setLenient() // 🔥 핵심: 비정상 JSON 허용
        .create()

    val api: TestApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // lenient 적용된 gson 사용
            .build()
            .create(TestApiService::class.java)
    }
}