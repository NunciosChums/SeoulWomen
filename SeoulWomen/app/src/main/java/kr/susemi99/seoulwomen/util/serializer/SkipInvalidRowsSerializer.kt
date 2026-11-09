package kr.susemi99.seoulwomen.util.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray
import kr.susemi99.seoulwomen.model.RowItem

/**
 * # SkipInvalidRowsSerializer
 * ### 형식이 잘못된 강좌를 건너뛰는 목록 변환 클래스
 * *일부 행의 날짜·시간 형식 오류로 페이지 전체가 실패하지 않게 함
 */
object SkipInvalidRowsSerializer : KSerializer<List<RowItem>> {
  private val delegate = ListSerializer(RowItem.serializer())

  override val descriptor: SerialDescriptor = delegate.descriptor
  override fun serialize(encoder: Encoder, value: List<RowItem>) = delegate.serialize(encoder, value)
  override fun deserialize(decoder: Decoder): List<RowItem> {
    val jsonDecoder = decoder as JsonDecoder
    return jsonDecoder.decodeJsonElement().jsonArray.mapNotNull { element ->
      runCatching { jsonDecoder.json.decodeFromJsonElement(RowItem.serializer(), element) }.getOrNull()
    }
  }
}
