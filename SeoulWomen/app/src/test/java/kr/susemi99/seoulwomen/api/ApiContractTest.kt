package kr.susemi99.seoulwomen.api

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kr.susemi99.seoulwomen.apiRespondingWith
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.fixture
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** 실제 API 응답을 앱과 같은 Retrofit·Json 설정으로 처리해 라이브러리 업그레이드 영향을 확인한다. */
class ApiContractTest {
  private val area = Area.Bukbu.className

  @Test
  fun `요청 URL에 센터 서비스 이름과 양끝 인덱스가 들어간다`() = runBlocking {
    var request: Request? = null
    val api = apiRespondingWith(fixture("list_success.json")) { request = it }

    api.list(areaValue = area, startIndex = 1, endIndex = 30)

    val url = checkNotNull(request).url
    assertEquals("openapi.seoul.go.kr", url.host)
    assertEquals(8088, url.port)
    assertTrue(url.encodedPath, url.encodedPath.endsWith("/json/SeoulBukbuWomenResourcesClass/1/30"))
  }

  @Test
  fun `성공 응답의 모든 형식을 파싱한다`() = runBlocking {
    val response = apiRespondingWith(fixture("list_success.json")).list(area, 1, 3)

    val classItem = checkNotNull(response.classItem)
    assertEquals(3, classItem.listTotalCount)
    assertEquals(ApiException.SUCCESS_CODE, response.resultItem?.code)
    assertEquals(3, classItem.rows.size)

    val row = classItem.rows.first()
    assertEquals("C100163836", row.classCode)
    assertEquals("연말 홈파티를 위한 칵테일(5회)(수강자격사전확인필수)", row.className)
    assertEquals("03", row.organCode)
    assertEquals("북부여성발전센터", row.organName)
    assertEquals("M", row.difficultyCode)
    assertEquals("중급", row.difficultyName)
    assertEquals(LocalDate.of(2026, 9, 10), row.receiveFrom)
    assertEquals(LocalDate.of(2026, 11, 18), row.receiveTo)
    assertEquals("09:00", row.receiveTimeFrom)
    assertEquals("18:00", row.receiveTimeTo)
    assertEquals(LocalDate.of(2026, 11, 12), row.educateFrom)
    assertEquals(LocalDate.of(2026, 12, 23), row.educateTo)
    assertEquals("18:30", row.educateTimeFrom)
    assertEquals("21:30", row.educateTimeTo)
    assertEquals("Y", row.saturday)
    assertEquals("N", row.sunday)
    assertEquals(12, row.collectNum)
    assertEquals(5, row.spareNum)
    assertEquals(75000, row.educateFee)
    assertEquals("Y", row.visitReceiveFlag)
    assertEquals("Y", row.onlineReceiveFlag)
    assertEquals("https://bukbu.seoulcareerup.or.kr/bukbu/edu/selectEduProgram.do?sch_class_code=C100163836", row.url)
  }

  @Test
  fun `날짜 형식이 잘못된 행만 건너뛰고 나머지를 읽는다`() = runBlocking {
    val body = Regex(""""RECEIVE_FROM"\s*:\s*"\d+"""").replaceFirst(fixture("list_success.json"), "\"RECEIVE_FROM\":\"\"")

    val rows = checkNotNull(apiRespondingWith(body).list(area, 1, 3).classItem).rows

    assertEquals(2, rows.size)
    assertTrue(rows.none { it.classCode == "C100163836" })
  }

  @Test
  fun `값이 null인 행도 건너뛴다`() = runBlocking {
    val body = Regex(""""RECEIVE_TIME_TO"\s*:\s*"[\d:]+"""").replaceFirst(fixture("list_success.json"), """"RECEIVE_TIME_TO":null""")

    assertEquals(2, checkNotNull(apiRespondingWith(body).list(area, 1, 3).classItem).rows.size)
  }

  @Test
  fun `결과 없음 응답은 최상위 RESULT로 파싱한다`() = runBlocking {
    val response = apiRespondingWith(fixture("list_no_data.json")).list(area, 99901, 99930)

    assertNull(response.classItem)
    assertEquals("INFO-200", response.resultItem?.code)
  }

  @Test
  fun `XML 오류 응답은 최상위 RESULT로 변환한다`() = runBlocking {
    val response = apiRespondingWith(fixture("list_invalid_key.xml")).list(area, 1, 30)

    assertNull(response.classItem)
    val result = checkNotNull(response.resultItem)
    assertEquals("INFO-100", result.code)
    assertTrue(result.message, result.message.startsWith("인증키가 유효하지 않습니다."))
    assertTrue(result.message, result.message.endsWith("신청하십시오."))
  }

  @Test
  fun `요청 범위 초과 XML 오류도 코드를 읽는다`() = runBlocking {
    val response = apiRespondingWith(fixture("list_error.xml")).list(area, 1, 2000)

    assertEquals("ERROR-336", response.resultItem?.code)
  }

  @Test
  fun `형식을 알 수 없는 응답은 SerializationException을 던진다`() {
    assertThrows(SerializationException::class.java) {
      runBlocking { apiRespondingWith("<html>Bad Gateway</html>").list(area, 1, 30) }
    }
  }
}
