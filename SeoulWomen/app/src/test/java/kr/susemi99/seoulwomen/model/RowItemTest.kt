package kr.susemi99.seoulwomen.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import kr.susemi99.seoulwomen.util.serializer.LocalDateSerializer
import kr.susemi99.seoulwomen.util.serializer.LocalTimeSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RowItemTest {
  private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
    isLenient = true
    serializersModule += SerializersModule {
      contextual(LocalDateSerializer)
      contextual(LocalTimeSerializer)
    }
  }

  private fun body(classCode: String = "C100162297", organCode: String = "25", fee: String = "262400.0") = """{
    "CLASS_CODE":"$classCode","CLASS_NAME":"강좌","ORGAN_CODE":"$organCode","ORGAN_NAME":"센터",
    "DIFFICULTY":"1","DIFFICULTY_NAME":"초급",
    "RECEIVE_FROM":"20260801","RECEIVE_TO":"20260810","RECEIVE_TIME_FROM":"09:00","RECEIVE_TIME_TO":"18:00",
    "EDUCATE_FROM":"20260901","EDUCATE_TO":"20260930","EDUCATE_TIME_FROM":"10:00","EDUCATE_TIME_TO":"12:00",
    "MONDAY":"Y","TUESDAY":null,"WEDNESDAY":null,"THURSDAY":null,"FRIDAY":null,"SATURDAY":null,"SUNDAY":null,
    "COLLECT_NUM":15.0,"SPARE_NUM":3.0,"EDUCATE_FEE":$fee,
    "VISIT_RECEIVE_FLAG":"Y","ONLINE_RECEIVE_FLAG":"N","URL":"https://example.com"
  }"""

  @Test
  fun `소수 형식 인원과 금액을 Int로 읽는다`() {
    val item = json.decodeFromString<RowItem>(body())
    assertEquals(15, item.collectNum)
    assertEquals(3, item.spareNum)
    assertEquals(262400, item.educateFee)
    assertEquals("3/15명", item.remainNumber)
    assertEquals("262,400원", item.fee)
  }

  @Test
  fun `수강료가 0이면 무료`() {
    assertEquals("무료", json.decodeFromString<RowItem>(body(fee = "0.0")).fee)
  }

  @Test
  fun `키는 organCode와 classCode 조합이며 재역직렬화해도 동일하다`() {
    val first = json.decodeFromString<RowItem>(body())
    val second = json.decodeFromString<RowItem>(body())
    assertEquals("25-C100162297", first.key)
    assertEquals(first.key, second.key)
    assertFalse(json.encodeToString(RowItem.serializer(), first).contains("\"id\""))
  }
}
