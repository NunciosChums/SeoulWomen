package kr.susemi99.seoulwomen.api

/**
 * 서울 열린데이터 API가 HTTP 200 응답 안에 `RESULT.CODE`로 실패를 알릴 때 던지는 예외.
 * `INFO-000`(정상)과 `INFO-200`(데이터 없음)을 제외한 코드(INFO-100: 인증키 오류, ERROR-xxx: 파라미터 오류 등)는 실패로 취급한다.
 */
class ApiException(val code: String, override val message: String) : Exception("[$code] $message") {
  companion object {
    const val SUCCESS_CODE = "INFO-000"
    const val NO_DATA_CODE = "INFO-200"
  }
}
