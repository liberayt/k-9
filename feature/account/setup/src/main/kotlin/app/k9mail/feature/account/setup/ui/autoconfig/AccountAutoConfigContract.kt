package app.k9mail.feature.account.setup.ui.autoconfig

import app.k9mail.core.common.domain.usecase.validation.ValidationResult
import app.k9mail.core.ui.compose.common.mvi.UnidirectionalViewModel
import app.k9mail.feature.account.setup.domain.entity.AutoDiscovery
import app.k9mail.feature.account.setup.domain.input.StringInputField

interface AccountAutoConfigContract {

    enum class ConfigStep {
        EMAIL_ADDRESS,
        OAUTH,
        PASSWORD,
    }

    interface ViewModel : UnidirectionalViewModel<State, Event, Effect> {
        fun initState(state: State)
    }

    data class State(
        val configStep: ConfigStep = ConfigStep.EMAIL_ADDRESS,
        val emailAddress: StringInputField = StringInputField(),
        val password: StringInputField = StringInputField(),
        val autoDiscovery: AutoDiscovery? = null,
        val error: Error? = null,
        val isLoading: Boolean = false,
    )

    sealed class Event {
        data class EmailAddressChanged(val emailAddress: String) : Event()
        data class PasswordChanged(val password: String) : Event()

        object OnRetryClicked : Event()
        object OnNextClicked : Event()
        object OnBackClicked : Event()
    }

    sealed class Effect {
        object NavigateNext : Effect()
        object NavigateBack : Effect()
    }

    interface Validator {
        fun validateEmailAddress(emailAddress: String): ValidationResult
        fun validatePassword(password: String): ValidationResult
    }

    sealed interface Error {
        object NetworkError : Error
        object UnknownError : Error
    }
}
