package kr.susemi99.seoulwomen.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kr.susemi99.seoulwomen.enums.Area

/**
 * 센터별 응답의 최상위 객체.
 *
 * 서버는 서비스 객체를 센터마다 다른 키(`SeoulDisableWomenResourcesClass` 등, [Area.className])로 내려준다.
 * 역직렬화 시 해당 키를 단일 키로 정규화하므로 센터가 추가될 때는 [Area]만 수정하면 된다.
 */
@Serializable(with = WomenResourcesClassParentItem.Serializer::class)
data class WomenResourcesClassParentItem(
  val classItem: WomenResourcesClassItem? = null,
  val result: ResultItem? = null
) {
  /**
   * 응답의 결과 코드. 성공 시에는 서비스 객체 내부의 RESULT, 실패 시에는 최상위 RESULT에 담겨 온다.
   */
  val resultItem: ResultItem?
    get() = classItem?.result ?: result

  @Serializable
  private class Surrogate(
    @SerialName(SERVICE_KEY) val classItem: WomenResourcesClassItem? = null,
    @SerialName("RESULT") val result: ResultItem? = null
  )

  /** [Area.className]에 해당하는 키를 [SERVICE_KEY]로 바꿔 [Surrogate]로 읽는다. */
  private object NormalizingSerializer : JsonTransformingSerializer<Surrogate>(Surrogate.serializer()) {
    private val classNames = Area.entries.map { it.className }.toSet()

    override fun transformDeserialize(element: JsonElement): JsonElement {
      if (element !is JsonObject) return element
      return JsonObject(element.entries.associate { (key, value) ->
        (if (key in classNames) SERVICE_KEY else key) to value
      })
    }
  }

  object Serializer : KSerializer<WomenResourcesClassParentItem> {
    override val descriptor: SerialDescriptor get() = NormalizingSerializer.descriptor

    override fun deserialize(decoder: Decoder): WomenResourcesClassParentItem =
      NormalizingSerializer.deserialize(decoder).let { WomenResourcesClassParentItem(it.classItem, it.result) }

    override fun serialize(encoder: Encoder, value: WomenResourcesClassParentItem) =
      NormalizingSerializer.serialize(encoder, Surrogate(value.classItem, value.result))
  }

  private companion object {
    const val SERVICE_KEY = "service"
  }
}
