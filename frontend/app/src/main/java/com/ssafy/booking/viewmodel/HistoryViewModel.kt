package com.ssafy.booking.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.CreateSummaryRequest
import com.ssafy.domain.model.history.CreateSummaryResponse
import com.ssafy.domain.model.history.LoadSummaryResponse
import com.ssafy.domain.model.history.Speaker
import com.ssafy.domain.model.history.TranscriptionResponse
import com.ssafy.domain.usecase.HistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCase: HistoryUseCase,
) : ViewModel() {
    var errorMessage = mutableStateOf("")
    private val _SpeakToTextInfo = MutableLiveData<TranscriptionResponse>()
    val SpeakToTextInfo: LiveData<TranscriptionResponse> = _SpeakToTextInfo

    private val _LoadSummaryInfo = MutableLiveData<LoadSummaryResponse>()
    val LoadSummaryInfo: LiveData<LoadSummaryResponse> = _LoadSummaryInfo
    private val _CreateSummaryInfo = MutableLiveData<CreateSummaryResponse>()
    val CreateSummaryInfo: LiveData<CreateSummaryResponse> = _CreateSummaryInfo

    private val _TransactionId = MutableLiveData<String>()
    val TransactionId: LiveData<String> = _TransactionId
    private val _SummaryInfo = MutableLiveData<String>()
    val SummaryInfo: LiveData<String> = _SummaryInfo

    fun loadTransaction(meetingInfoId: Long) {
        viewModelScope.launch {
            try {
                val transaction = historyUseCase.getSpeakToText(meetingInfoId)
                _SpeakToTextInfo.value = transaction
                Log.d("STT_TEST", "HVM STTINFO) ID ${SpeakToTextInfo.value}")
                _TransactionId.value = transaction.id
                _SummaryInfo.value = transaction.text
                if(transaction != null) {
                    Log.d("STT_TEST", "loadsummarybefore $transaction")
                    loadSummary(transaction.text, transaction.id, meetingInfoId)
                }
            } catch (e: Exception) {
                errorMessage.value = "HVM SUMMARY 네트워크 에러: ${e}"
                Log.d("STT_TEST", "$errorMessage.value")
            }
        }
    }

    fun loadSummary(content: String, transactionId: String, meetingInfoId: Long) {
        viewModelScope.launch {
            val transaction = historyUseCase.getSpeakToText(meetingInfoId)
            try {
                val response = historyUseCase.getSummary(transactionId)
                Log.d("STT_TEST", "loadSummary response $response")
                // 요약 있음
            } catch (e: Exception) {
                errorMessage.value = "HVM Load Summary 네트워크 에러: ${e}"
                Log.d("STT_TEST", "$errorMessage.value")
                // 요약 없음
                Log.d("STT_TEST", "예외처리 현재 상태 transactionId:$transactionId transaction:$transaction content:$content")
                val summaryRequest = CreateSummaryRequest(
                    content = transaction.text,
                    transcriptionId = transaction.id
                )

                Log.d("STT_TEST", "요약 생성을 위한 $summaryRequest")
                if (content.length <= 100) {
                  summaryRequest.content = "\n" +
                          "안녕하십니까?\n" +
                          "북킹의 녹음 모임 요약은 NAVER CLOUD SUMMARY입니다.\n" +
                          "해당 서비스를 이용하기 위해선 올바른 내용과 글자 수 제한이 있습니다.\n" +
                          "지금 이 글을 보고 계신다면 해당  조건을 만족하지 못했기때문입니다.\n" +
                          "감사합니다."
                }
                Log.d("STT_TEST", "요약 생성 전 예외처리 이후 $summaryRequest")
                createSummary(summaryRequest)
            }
        }
    }

    fun createSummary(request: CreateSummaryRequest) {
        viewModelScope.launch {
            Log.d("STT_TEST", "CreateSummaryRequest $request")
            try {
                val response = historyUseCase.createSummary(request)
                _CreateSummaryInfo.value = response
                _SummaryInfo.value = response.summary
                Log.d("STT_TEST", "createSummary response ${response}")
            }  catch (e:Exception) {
                errorMessage.value = "HVM SUMMARY 네트워크 에러: ${e}"
                Log.d("STT_TEST", "$errorMessage.value")
            }
        }
    }
    
    // 대화 내용 수정
    // 스피커 VALUE 수정
    
    // 대사 조정


}
