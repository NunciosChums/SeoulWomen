package kr.susemi99.seoulwomen.model

import kotlinx.serialization.json.Json
import kr.susemi99.seoulwomen.enums.Area
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class WomenResourcesClassParentItemTest {
  private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true; isLenient = true }

  @Test
  fun `모든 센터 키를 classItem으로 읽는다`() {
    Area.entries.forEach { area ->
      val body = """{"${area.className}":{"list_total_count":1,"RESULT":{"CODE":"INFO-000","MESSAGE":"ok"},"row":[]}}"""
      val item = json.decodeFromString<WomenResourcesClassParentItem>(body)
      assertNotNull(area.name, item.classItem)
      assertEquals("INFO-000", item.resultItem?.code)
    }
  }

  @Test
  fun `실패 응답은 최상위 RESULT를 읽는다`() {
    val item = json.decodeFromString<WomenResourcesClassParentItem>("""{"RESULT":{"CODE":"ERROR-300","MESSAGE":"fail"}}""")
    assertNull(item.classItem)
    assertEquals("ERROR-300", item.resultItem?.code)
  }
}
