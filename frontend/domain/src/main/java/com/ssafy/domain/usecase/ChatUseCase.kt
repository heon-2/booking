package com.ssafy.domain.usecase

import com.ssafy.domain.model.ChatCreateRequest
import com.ssafy.domain.model.ChatExitRequest
import com.ssafy.domain.model.ChatJoinRequest
import com.ssafy.domain.repository.ChatRepository
import retrofit2.Response
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend fun createChat(request: ChatCreateRequest): Response<Unit> {
        return repository.postChatCreate(request)
    }
    suspend fun joinChat(request: ChatJoinRequest): Response<Unit> {
        return repository.postChatJoin(request)
    }

    suspend fun exitChat(request: ChatExitRequest): Response<Unit> {
        return repository.postChatExit(request)
    }

}