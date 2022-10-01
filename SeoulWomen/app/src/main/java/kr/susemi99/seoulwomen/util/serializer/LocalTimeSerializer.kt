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
 * # LocalTimeSerializer
 * ### 시간 변환 클래스
 * *"HH:mm" 형식을 [LocalTime]으로 변환
 * @see LocalTimeSerializer
 */
object LocalTimeSerializer : KSerializer<LocalTime> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: LocalTime) = encoder.encodeString(value.toString())
  override fun deserialize(decoder: Decoder): LocalTime = LocalTime.parse(decoder.decodeString(), DateTimeFormatter.ofPattern("HH:mm"))
}