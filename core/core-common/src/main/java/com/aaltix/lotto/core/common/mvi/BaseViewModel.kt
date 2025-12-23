package com.aaltix.lotto.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementing the MVI (Model-View-Intent) pattern.
 *
 * @param State The UI state type, should be an immutable data class
 * @param Intent User actions/events that can modify the state
 * @param Effect One-time side effects (navigation, toasts, etc.)
 * @param initialState The initial state when the ViewModel is created
 */
abstract class BaseViewModel<State, Intent, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    /**
     * Returns the current state value.
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Updates the state using a reducer function.
     * The reducer receives the current state and returns the new state.
     */
    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    /**
     * Sends a one-time side effect to the UI.
     * Effects are emitted once and are not replayed on configuration changes.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * Process an intent from the UI.
     * This is the main entry point for UI events.
     */
    fun processIntent(intent: Intent) {
        handleIntent(intent)
    }

    /**
     * Handle the intent and update state/effects accordingly.
     * Subclasses must implement this to define behavior for each intent.
     */
    protected abstract fun handleIntent(intent: Intent)
}
