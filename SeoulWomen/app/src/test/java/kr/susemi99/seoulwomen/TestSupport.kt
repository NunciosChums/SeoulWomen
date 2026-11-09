package kr.susemi99.seoulwomen

import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.di.NetworkModule
import kr.susemi99.seoulwomen.model.RowItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.LocalDate

/** `src/test/resources/fixtures` 에 저장한 실제 API 응답을 읽는다. */
fun fixture(name: String): String =
  checkNotNull(RowItem::class.java.classLoader?.getResourceAsStream("fixtures/$name")) { "fixture 없음: $name" }
    .bufferedReader()
    .use { it.readText() }

/**
 * 네트워크 없이 항상 [body]를 HTTP 200으로 돌려주는 [Api].
 * 앱과 같은 [NetworkModule] 설정을 쓰므로 Retrofit, converter, Json 경로 전체를 검증한다.
 */
fun apiRespondingWith(body: String, onRequest: (Request) -> Unit = {}): Api {
  val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
      onRequest(chain.request())
      Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(body.toResponseBody("application/json".toMediaType()))
        .build()
    }
    .build()
  val retrofit = NetworkModule.provideRetrofit(client, NetworkModule.provideJson())
  return NetworkModule.provideApiService(retrofit)
}

fun rowItem(classCode: String = "C1") = RowItem(
  classCode = classCode,
  className = "테스트 강좌",
  organCode = "03",
  organName = "테스트 센터",
  difficultyCode = "J",
  difficultyName = "초급",
  receiveFrom = LocalDate.of(2026, 3, 2),
  receiveTo = LocalDate.of(2026, 3, 6),
  receiveTimeFrom = "09:00",
  receiveTimeTo = "18:00",
  educateFrom = LocalDate.of(2026, 3, 9),
  educateTo = LocalDate.of(2026, 3, 13),
  educateTimeFrom = "14:00",
  educateTimeTo = "17:00",
  monday = "Y",
  tuesday = "N",
  wednesday = "Y",
  thursday = "N",
  friday = "Y",
  saturday = "N",
  sunday = "N",
  collectNum = 20,
  spareNum = 5,
  educateFee = 100000,
  visitReceiveFlag = "Y",
  onlineReceiveFlag = "Y",
  url = "https://example.com/class",
)
