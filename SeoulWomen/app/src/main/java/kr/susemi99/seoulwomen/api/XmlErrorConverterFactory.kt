package kr.susemi99.seoulwomen.api

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 서울 열린데이터광장 API는 json 경로로 요청해도 일부 오류(INFO-100, ERROR-336 등)를 XML로 응답한다.
 * 이런 응답을 같은 의미의 JSON(최상위 `RESULT`)으로 바꿔 JSON 오류와 같은 경로로 처리되게 한다.
 */
class XmlErrorConverterFactory : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit,
  ): Converter<ResponseBody, *> {
    val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
    return Converter<ResponseBody, Any> { body ->
      val contentType = body.contentType()
      val text = body.string()
      delegate.convert((xmlErrorToJson(text) ?: text).toResponseBody(contentType))
    }
  }

  private fun xmlErrorToJson(text: String): String? {
    val (code, message) = XML_ERROR.find(text)?.destructured ?: return null
    return buildJsonObject {
      putJsonObject("RESULT") {
        put("CODE", code)
        put("MESSAGE", message)
      }
    }.toString()
  }

  private companion object {
    val XML_ERROR = Regex(
      """^\s*<RESULT>\s*<CODE>(.*?)</CODE>\s*<MESSAGE>(?:<!\[CDATA\[)?(.*?)(?:]]>)?</MESSAGE>""",
      RegexOption.DOT_MATCHES_ALL,
    )
  }
}
