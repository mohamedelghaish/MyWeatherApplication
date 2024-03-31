package com.example.myweatherapplication.settings

import com.example.myweatherapplication.Const
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LanguageHandler(
    private val externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 5000) {

    private val _languageFlow = MutableSharedFlow<String>(replay = 3)
    val languageFlow: SharedFlow<String> = _languageFlow

    val currentLanguage = Const.language

    init {
        externalScope.launch {
            while(true) {
                _languageFlow.emit(currentLanguage)
                delay(tickIntervalMs)
            }
        }
    }


}