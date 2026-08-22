package kr.susemi99.seoulwomen.util.serializer

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalTime
import java.time.format.DateTimeParseException

class LocalTimeSerializerTest {
  private val json = Json

  @Test
  fun `HH mm 문자열을 LocalTime으로 변환한다`() {
    assertEquals(LocalTime.of(9, 30), json.decodeFromString(LocalTimeSerializer, "\"09:30\""))
  }

  @Test
  fun `경계값 00 00 과 23 59 를 변환한다`() {
    assertEquals(LocalTime.MIDNIGHT, json.decodeFromString(LocalTimeSerializer, "\"00:00\""))
    assertEquals(LocalTime.of(23, 59), json.decodeFromString(LocalTimeSerializer, "\"23:59\""))
  }

  @Test
  fun `HH mm 형식으로 직렬화한다`() {
    assertEquals("\"09:30\"", json.encodeToString(LocalTimeSerializer, LocalTime.of(9, 30)))
  }

  @Test
  fun `초가 포함된 형식은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalTimeSerializer, "\"09:30:00\"")
    }
  }

  @Test
  fun `한 자리 시간은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalTimeSerializer, "\"9:30\"")
    }
  }

  @Test
  fun `24 00 은 SMART 해석으로 자정이 된다`() {
    // DateTimeFormatter 기본 ResolverStyle.SMART 동작
    assertEquals(LocalTime.MIDNIGHT, json.decodeFromString(LocalTimeSerializer, "\"24:00\""))
  }

  @Test
  fun `범위를 벗어난 시간은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalTimeSerializer, "\"25:00\"")
    }
  }

  @Test
  fun `범위를 벗어난 분은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalTimeSerializer, "\"10:60\"")
    }
  }

  @Test
  fun `빈 문자열은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalTimeSerializer, "\"\"")
    }
  }
}
