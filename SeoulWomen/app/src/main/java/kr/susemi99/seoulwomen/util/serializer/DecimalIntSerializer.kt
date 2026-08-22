package kr.susemi99.seoulwomen.util.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * # DecimalIntSerializer
 * ### 소수 형식 숫자를 [Int]로 변환
 * *API가 `15.0`, `262400.0`처럼 내려주는 인원·금액 값을 [Int]로 읽음
 */
object DecimalIntSerializer : KSerializer<Int> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DecimalInt", PrimitiveKind.DOUBLE)
  override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
  override fun deserialize(decoder: Decoder): Int = decoder.decodeDouble().toInt()
}
