package kr.susemi99.seoulwomen.util.serializer

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeParseException

class LocalDateSerializerTest {
  private val json = Json

  @Test
  fun `yyyyMMdd 문자열을 LocalDate로 변환한다`() {
    assertEquals(LocalDate.of(2026, 8, 22), json.decodeFromString(LocalDateSerializer, "\"20260822\""))
  }

  @Test
  fun `윤년 날짜를 변환한다`() {
    assertEquals(LocalDate.of(2024, 2, 29), json.decodeFromString(LocalDateSerializer, "\"20240229\""))
  }

  @Test
  fun `ISO 형식으로 직렬화한다`() {
    assertEquals("\"2026-08-22\"", json.encodeToString(LocalDateSerializer, LocalDate.of(2026, 8, 22)))
  }

  @Test
  fun `하이픈이 포함된 형식은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalDateSerializer, "\"2026-08-22\"")
    }
  }

  @Test
  fun `존재하지 않는 일자는 SMART 해석으로 해당 월 말일로 보정된다`() {
    // DateTimeFormatter 기본 ResolverStyle.SMART 동작: 2월 30일 → 2월 28일
    assertEquals(LocalDate.of(2026, 2, 28), json.decodeFromString(LocalDateSerializer, "\"20260230\""))
  }

  @Test
  fun `존재하지 않는 월은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalDateSerializer, "\"20261301\"")
    }
  }

  @Test
  fun `빈 문자열은 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalDateSerializer, "\"\"")
    }
  }

  @Test
  fun `자릿수가 모자라면 실패한다`() {
    assertThrows(DateTimeParseException::class.java) {
      json.decodeFromString(LocalDateSerializer, "\"2026822\"")
    }
  }
}
