package kr.susemi99.seoulwomen.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import kr.susemi99.seoulwomen.util.serializer.LocalDateSerializer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.util.Locale

class RowItemTest {
  private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    serializersModule += SerializersModule {
      contextual(LocalDateSerializer)
    }
  }

  private lateinit var defaultLocale: Locale

  @Before
  fun setUp() {
    // dayName 은 기기 언어와 관계없이 한국어여야 하므로 한국어가 아닌 기본 로케일에서 검증한다.
    defaultLocale = Locale.getDefault()
    Locale.setDefault(Locale.US)
  }

  @After
  fun tearDown() {
    Locale.setDefault(defaultLocale)
  }

  private fun body(
    classCode: String = "C100162297",
    organCode: String = "25",
    fee: String = "262400.0",
    receiveFrom: String = "20260801",
    receiveTo: String = "20260810",
    receiveTimeTo: String = "18:00",
    educateFrom: String = "20260901",
    educateTo: String = "20260930",
    monday: String? = "Y",
    tuesday: String? = null,
    wednesday: String? = null,
    thursday: String? = null,
    friday: String? = null,
    saturday: String? = null,
    sunday: String? = null,
    visitFlag: String = "Y",
    onlineFlag: String = "N"
  ): String {
    fun String?.json() = if (this == null) "null" else "\"$this\""
    return """{
    "CLASS_CODE":"$classCode","CLASS_NAME":"강좌","ORGAN_CODE":"$organCode","ORGAN_NAME":"센터",
    "DIFFICULTY":"1","DIFFICULTY_NAME":"초급",
    "RECEIVE_FROM":"$receiveFrom","RECEIVE_TO":"$receiveTo","RECEIVE_TIME_FROM":"09:00","RECEIVE_TIME_TO":"$receiveTimeTo",
    "EDUCATE_FROM":"$educateFrom","EDUCATE_TO":"$educateTo","EDUCATE_TIME_FROM":"10:00","EDUCATE_TIME_TO":"12:00",
    "MONDAY":${monday.json()},"TUESDAY":${tuesday.json()},"WEDNESDAY":${wednesday.json()},"THURSDAY":${thursday.json()},
    "FRIDAY":${friday.json()},"SATURDAY":${saturday.json()},"SUNDAY":${sunday.json()},
    "COLLECT_NUM":15.0,"SPARE_NUM":3.0,"EDUCATE_FEE":$fee,
    "VISIT_RECEIVE_FLAG":"$visitFlag","ONLINE_RECEIVE_FLAG":"$onlineFlag","URL":"https://example.com"
  }"""
  }

  private fun decode(body: String) = json.decodeFromString<RowItem>(body)

  @Test
  fun `소수 형식 인원과 금액을 Int로 읽는다`() {
    val item = decode(body())
    assertEquals(15, item.collectNum)
    assertEquals(3, item.spareNum)
    assertEquals(262400, item.educateFee)
    assertEquals("3/15명", item.remainNumber)
    assertEquals("262,400원", item.fee)
  }

  @Test
  fun `수강료가 0이면 무료`() {
    assertEquals("무료", decode(body(fee = "0.0")).fee)
  }

  @Test
  fun `수강료가 음수여도 무료`() {
    assertEquals("무료", decode(body(fee = "-100.0")).fee)
  }

  @Test
  fun `수강료가 1000 미만이면 천 단위 구분자가 없다`() {
    assertEquals("500원", decode(body(fee = "500.0")).fee)
  }

  @Test
  fun `키는 organCode와 classCode 조합이며 재역직렬화해도 동일하다`() {
    val first = decode(body())
    val second = decode(body())
    assertEquals("25-C100162297", first.key)
    assertEquals(first.key, second.key)
    assertFalse(json.encodeToString(RowItem.serializer(), first).contains("\"id\""))
  }

  @Test
  fun `난이도는 대괄호로 감싼다`() {
    assertEquals("[초급]", decode(body()).difficulty)
  }

  // receivePeriod

  @Test
  fun `신청 시작일과 종료일이 다르면 두 줄로 표시한다`() {
    assertEquals("2026-08-01(토) 09:00 ~\n2026-08-10(월) 18:00", decode(body()).receivePeriod)
  }

  @Test
  fun `신청 시작일과 종료일이 같으면 한 줄로 표시한다`() {
    val item = decode(body(receiveFrom = "20260801", receiveTo = "20260801"))
    assertEquals("2026-08-01(토) 09:00 ~ 18:00", item.receivePeriod)
  }

  @Test
  fun `24시 마감은 서버 값 그대로 표시한다`() {
    val item = decode(body(receiveTimeTo = "24:00"))
    assertEquals("2026-08-01(토) 09:00 ~\n2026-08-10(월) 24:00", item.receivePeriod)
  }

  // educateSchedule

  @Test
  fun `교육 시작일과 종료일이 다르면 기간 요일 시간을 줄바꿈으로 표시한다`() {
    assertEquals("$EDUCATE_RANGE\n월\n10:00 ~ 12:00", decode(body()).educateSchedule)
  }

  @Test
  fun `하루짜리 교육은 날짜와 시간을 한 줄로 표시한다`() {
    val item = decode(body(educateFrom = "20260905", educateTo = "20260905"))
    assertEquals("2026-09-05(토) 10:00 ~ 12:00", item.educateSchedule)
  }

  @Test
  fun `기간 교육은 요일과 시간을 표시한다`() {
    val item = decode(body(monday = "Y", wednesday = "Y", friday = "Y"))
    assertEquals("$EDUCATE_RANGE\n월수금\n10:00 ~ 12:00", item.educateSchedule)
  }

  @Test
  fun `모든 요일이 설정되면 월화수목금토일 순서로 표시한다`() {
    val item = decode(
      body(monday = "Y", tuesday = "Y", wednesday = "Y", thursday = "Y", friday = "Y", saturday = "Y", sunday = "Y")
    )
    assertEquals("$EDUCATE_RANGE\n월화수목금토일\n10:00 ~ 12:00", item.educateSchedule)
  }

  @Test
  fun `요일 값이 빈 문자열이면 요일로 치지 않는다`() {
    val item = decode(body(monday = "", tuesday = " ", sunday = "Y"))
    assertEquals("$EDUCATE_RANGE\n일\n10:00 ~ 12:00", item.educateSchedule)
  }

  // API는 진행하지 않는 요일을 "N"으로 보낸다.
  @Test
  fun `진행하지 않는 요일 N은 표시하지 않는다`() {
    val item = decode(
      body(monday = "Y", tuesday = "Y", wednesday = "Y", thursday = "Y", friday = "Y", saturday = "N", sunday = "N")
    )
    assertEquals("$EDUCATE_RANGE\n월화수목금\n10:00 ~ 12:00", item.educateSchedule)
  }

  @Test
  fun `요일이 하나도 없으면 빈 요일 줄과 시간만 표시한다`() {
    val item = decode(body(monday = null))
    assertEquals("$EDUCATE_RANGE\n\n10:00 ~ 12:00", item.educateSchedule)
  }

  // howToRegister

  @Test
  fun `방문 접수만 가능`() {
    assertEquals("방문", decode(body(visitFlag = "Y", onlineFlag = "N")).howToRegister)
  }

  @Test
  fun `온라인 접수만 가능`() {
    assertEquals("온라인", decode(body(visitFlag = "N", onlineFlag = "Y")).howToRegister)
  }

  @Test
  fun `방문과 온라인 모두 가능하면 쉼표로 나열한다`() {
    assertEquals("방문, 온라인", decode(body(visitFlag = "Y", onlineFlag = "Y")).howToRegister)
  }

  @Test
  fun `둘 다 불가능하면 접수방법 확인`() {
    assertEquals("접수방법 확인", decode(body(visitFlag = "N", onlineFlag = "N")).howToRegister)
  }

  @Test
  fun `플래그가 공백이나 알 수 없는 값이면 접수방법 확인`() {
    assertEquals("접수방법 확인", decode(body(visitFlag = "", onlineFlag = "X")).howToRegister)
  }

  private companion object {
    const val EDUCATE_RANGE = "2026-09-01(화) ~ 2026-09-30(수)"
  }
}
