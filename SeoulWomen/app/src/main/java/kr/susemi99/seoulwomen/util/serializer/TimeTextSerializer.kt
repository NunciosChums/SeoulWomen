package kr.susemi99.seoulwomen.util.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * # TimeTextSerializer
 * ### 시간 문자열 검증 클래스
 * *"HH:mm" 형식인지 확인하고 서버 값을 그대로 돌려줌
 * *자정 마감을 뜻하는 "24:00"은 [LocalTime]으로 바꾸면 "00:00"이 되므로 문자열로 보존함
 */
object TimeTextSerializer : KSerializer<String> {
  private val FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimeText", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
  override fun deserialize(decoder: Decoder): String = decoder.decodeString().also { LocalTime.parse(it, FORMATTER) }
}
