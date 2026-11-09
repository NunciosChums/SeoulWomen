package kr.susemi99.seoulwomen.util.serializer

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.format.DateTimeParseException

class TimeTextSerializerTest {
  private val json = Json

  /** kotlinx.serialization 1.12부터 deserializer의 예외는 SerializationException으로 감싸져 전달된다 */
  private fun assertParseFails(jsonString: String) {
    val exception = assertThrows(SerializationException::class.java) {
      json.decodeFromString(TimeTextSerializer, jsonString)
    }
    assertTrue(exception.cause is DateTimeParseException)
  }

  @Test
  fun `HH mm 문자열을 그대로 읽는다`() {
    assertEquals("09:30", json.decodeFromString(TimeTextSerializer, "\"09:30\""))
  }

  @Test
  fun `경계값 00 00 과 23 59 를 읽는다`() {
    assertEquals("00:00", json.decodeFromString(TimeTextSerializer, "\"00:00\""))
    assertEquals("23:59", json.decodeFromString(TimeTextSerializer, "\"23:59\""))
  }

  @Test
  fun `24 00 은 00 00 으로 바뀌지 않고 그대로 보존한다`() {
    assertEquals("24:00", json.decodeFromString(TimeTextSerializer, "\"24:00\""))
  }

  @Test
  fun `읽은 값과 같은 형식으로 직렬화한다`() {
    assertEquals("\"24:00\"", json.encodeToString(TimeTextSerializer, "24:00"))
  }

  @Test
  fun `초가 포함된 형식은 실패한다`() {
    assertParseFails("\"09:30:00\"")
  }

  @Test
  fun `한 자리 시간은 실패한다`() {
    assertParseFails("\"9:30\"")
  }

  @Test
  fun `범위를 벗어난 시간은 실패한다`() {
    assertParseFails("\"25:00\"")
  }

  @Test
  fun `범위를 벗어난 분은 실패한다`() {
    assertParseFails("\"10:60\"")
  }

  @Test
  fun `빈 문자열은 실패한다`() {
    assertParseFails("\"\"")
  }
}
