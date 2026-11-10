package kr.susemi99.seoulwomen.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import kr.susemi99.seoulwomen.util.serializer.LocalDateSerializer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
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
    receiveTimeFrom: String = "09:00",
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
    collectNum: String = "15.0",
    spareNum: String = "3.0",
    visitFlag: String = "Y",
    onlineFlag: String = "N"
  ): String {
    fun String?.json() = if (this == null) "null" else "\"$this\""
    return """{
    "CLASS_CODE":"$classCode","CLASS_NAME":"강좌","ORGAN_CODE":"$organCode","ORGAN_NAME":"센터",
    "DIFFICULTY":"1","DIFFICULTY_NAME":"초급",
    "RECEIVE_FROM":"$receiveFrom","RECEIVE_TO":"$receiveTo","RECEIVE_TIME_FROM":"$receiveTimeFrom","RECEIVE_TIME_TO":"$receiveTimeTo",
    "EDUCATE_FROM":"$educateFrom","EDUCATE_TO":"$educateTo","EDUCATE_TIME_FROM":"10:00","EDUCATE_TIME_TO":"12:00",
    "MONDAY":${monday.json()},"TUESDAY":${tuesday.json()},"WEDNESDAY":${wednesday.json()},"THURSDAY":${thursday.json()},
    "FRIDAY":${friday.json()},"SATURDAY":${saturday.json()},"SUNDAY":${sunday.json()},
    "COLLECT_NUM":$collectNum,"SPARE_NUM":$spareNum,"EDUCATE_FEE":$fee,
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
    assertEquals("262,400원", item.fee)
  }

  @Test
  fun `수강료가 0이면 무료`() {
    val item = decode(body(fee = "0.0"))
    assertEquals("무료", item.fee)
    assertTrue(item.isFree)
    assertFalse(decode(body()).isFree)
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

  // receiveStatus

  @Test
  fun `접수 시작 시각 전이면 접수 예정`() {
    assertEquals(ReceiveStatus.Upcoming, decode(body()).receiveStatus(at(2026, 7, 31, 23, 59)))
    assertEquals(ReceiveStatus.Upcoming, decode(body()).receiveStatus(at(2026, 8, 1, 8, 59)))
  }

  @Test
  fun `접수 시작 시각부터 접수 중이며 마감일까지 남은 날 수를 알려준다`() {
    assertEquals(ReceiveStatus.Open(daysLeft = 9), decode(body()).receiveStatus(at(2026, 8, 1, 9, 0)))
    assertEquals(ReceiveStatus.Open(daysLeft = 3), decode(body()).receiveStatus(at(2026, 8, 7, 12, 0)))
  }

  @Test
  fun `마감일 당일 마감 시각 전이면 남은 날 수가 0이다`() {
    assertEquals(ReceiveStatus.Open(daysLeft = 0), decode(body()).receiveStatus(at(2026, 8, 10, 17, 59)))
  }

  @Test
  fun `마감일 당일이라도 마감 시각이 되면 접수 마감`() {
    assertEquals(ReceiveStatus.Closed, decode(body()).receiveStatus(at(2026, 8, 10, 18, 0)))
    assertEquals(ReceiveStatus.Closed, decode(body()).receiveStatus(at(2026, 8, 11, 0, 0)))
  }

  @Test
  fun `24시 마감은 마감일 자정 직전까지 접수 중이다`() {
    val item = decode(body(receiveTimeTo = "24:00"))
    assertEquals(ReceiveStatus.Open(daysLeft = 0), item.receiveStatus(at(2026, 8, 10, 23, 59)))
    assertEquals(ReceiveStatus.Closed, item.receiveStatus(at(2026, 8, 11, 0, 0)))
  }

  // receivePeriod

  @Test
  fun `신청 시작일과 종료일이 다르면 날짜와 시각을 물결로 잇는다`() {
    assertEquals("8.1(토) 09:00 ~ 8.10(월) 18:00", decode(body()).receivePeriod(TODAY))
  }

  @Test
  fun `신청 시작일과 종료일이 같으면 날짜를 한 번만 표시한다`() {
    val item = decode(body(receiveFrom = "20260801", receiveTo = "20260801"))
    assertEquals("8.1(토) 09:00~18:00", item.receivePeriod(TODAY))
  }

  @Test
  fun `24시 마감은 서버 값 그대로 표시한다`() {
    val item = decode(body(receiveTimeTo = "24:00"))
    assertEquals("8.1(토) 09:00 ~ 8.10(월) 24:00", item.receivePeriod(TODAY))
  }

  @Test
  fun `올해가 아닌 날짜에만 연도를 붙인다`() {
    val item = decode(body(receiveFrom = "20261228", receiveTo = "20270105", educateFrom = "20270111", educateTo = "20270129"))
    assertEquals("12.28(월) 09:00 ~ 2027.1.5(화) 18:00", item.receivePeriod(TODAY))
    assertEquals("2027.1.11 ~ 2027.1.29 · 월 10:00~12:00", item.educateSchedule(TODAY))
    assertEquals("2026.12.28(월) 09:00 ~ 1.5(화) 18:00", item.receivePeriod(LocalDate.of(2027, 1, 2)))
  }

  // educateSchedule

  @Test
  fun `하루짜리 교육은 요일을 붙인 날짜와 시간을 표시한다`() {
    val item = decode(body(educateFrom = "20260905", educateTo = "20260905"))
    assertEquals("9.5(토) 10:00~12:00", item.educateSchedule(TODAY))
  }

  @Test
  fun `기간 교육은 기간 요일 시간을 한 줄로 표시한다`() {
    val item = decode(body(monday = "Y", wednesday = "Y", friday = "Y"))
    assertEquals("$EDUCATE_RANGE · 월·수·금 10:00~12:00", item.educateSchedule(TODAY))
  }

  @Test
  fun `모든 요일이 설정되면 월화수목금토일 순서로 표시한다`() {
    val item = decode(
      body(monday = "Y", tuesday = "Y", wednesday = "Y", thursday = "Y", friday = "Y", saturday = "Y", sunday = "Y")
    )
    assertEquals("$EDUCATE_RANGE · 월·화·수·목·금·토·일 10:00~12:00", item.educateSchedule(TODAY))
  }

  @Test
  fun `요일 값이 빈 문자열이면 요일로 치지 않는다`() {
    val item = decode(body(monday = "", tuesday = " ", sunday = "Y"))
    assertEquals("$EDUCATE_RANGE · 일 10:00~12:00", item.educateSchedule(TODAY))
  }

  // API는 진행하지 않는 요일을 "N"으로 보낸다.
  @Test
  fun `진행하지 않는 요일 N은 표시하지 않는다`() {
    val item = decode(
      body(monday = "Y", tuesday = "Y", wednesday = "Y", thursday = "Y", friday = "Y", saturday = "N", sunday = "N")
    )
    assertEquals("$EDUCATE_RANGE · 월·화·수·목·금 10:00~12:00", item.educateSchedule(TODAY))
  }

  @Test
  fun `요일이 하나도 없으면 기간과 시간만 표시한다`() {
    val item = decode(body(monday = null))
    assertEquals("$EDUCATE_RANGE · 10:00~12:00", item.educateSchedule(TODAY))
  }

  // 잔여

  @Test
  fun `남은 자리가 있으면 정원 마감이 아니고 신청 비율을 계산한다`() {
    val item = decode(body(collectNum = "20.0", spareNum = "5.0"))
    assertFalse(item.isFull)
    assertFalse(item.isAlmostFull)
    assertEquals(0.75f, item.enrolledRatio)
  }

  @Test
  fun `남은 자리가 정원의 20퍼센트 이하면 마감 임박`() {
    assertTrue(decode(body(collectNum = "20.0", spareNum = "4.0")).isAlmostFull)
    assertTrue(decode(body(collectNum = "15.0", spareNum = "3.0")).isAlmostFull)
    assertFalse(decode(body(collectNum = "15.0", spareNum = "4.0")).isAlmostFull)
  }

  @Test
  fun `남은 자리가 없으면 정원 마감이며 마감 임박은 아니다`() {
    val item = decode(body(collectNum = "15.0", spareNum = "0.0"))
    assertTrue(item.isFull)
    assertFalse(item.isAlmostFull)
    assertEquals(1f, item.enrolledRatio)
  }

  @Test
  fun `인원 값이 비정상이어도 신청 비율은 0과 1 사이다`() {
    assertEquals(0f, decode(body(collectNum = "0.0", spareNum = "3.0")).enrolledRatio)
    assertEquals(0f, decode(body(collectNum = "10.0", spareNum = "12.0")).enrolledRatio)
    assertEquals(1f, decode(body(collectNum = "10.0", spareNum = "-1.0")).enrolledRatio)
  }

  // registerMethods

  @Test
  fun `방문 접수만 가능`() {
    assertEquals(listOf("방문"), decode(body(visitFlag = "Y", onlineFlag = "N")).registerMethods)
  }

  @Test
  fun `온라인 접수만 가능`() {
    assertEquals(listOf("온라인"), decode(body(visitFlag = "N", onlineFlag = "Y")).registerMethods)
  }

  @Test
  fun `방문과 온라인 모두 가능하면 방문 온라인 순서로 나열한다`() {
    assertEquals(listOf("방문", "온라인"), decode(body(visitFlag = "Y", onlineFlag = "Y")).registerMethods)
  }

  @Test
  fun `둘 다 불가능하면 접수방법 확인`() {
    assertEquals(listOf("접수방법 확인"), decode(body(visitFlag = "N", onlineFlag = "N")).registerMethods)
  }

  @Test
  fun `플래그가 공백이나 알 수 없는 값이면 접수방법 확인`() {
    assertEquals(listOf("접수방법 확인"), decode(body(visitFlag = "", onlineFlag = "X")).registerMethods)
  }

  private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int) = LocalDateTime.of(year, month, day, hour, minute)

  private companion object {
    val TODAY: LocalDate = LocalDate.of(2026, 7, 1)
    const val EDUCATE_RANGE = "9.1 ~ 9.30"
  }
}
