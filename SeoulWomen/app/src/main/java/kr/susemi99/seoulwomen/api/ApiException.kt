package kr.susemi99.seoulwomen.api

/**
 * 서울 열린데이터 API가 HTTP 200 응답 안에 `RESULT.CODE`로 실패를 알릴 때 던지는 예외.
 * 정상 코드는 `INFO-000`이며, 그 외(INFO-200: 데이터 없음, ERROR-xxx: 키 만료/파라미터 오류 등)는 실패로 취급한다.
 */
class ApiException(val code: String, override val message: String) : Exception("[$code] $message") {
  companion object {
    const val SUCCESS_CODE = "INFO-000"
  }
}
