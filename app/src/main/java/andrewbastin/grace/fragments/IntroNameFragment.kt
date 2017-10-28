package andrewbastin.grace.fragments

import agency.tango.materialintroscreen.SlideFragment
import andrewbastin.grace.R
import andrewbastin.grace.extensions.bind
import andrewbastin.grace.singletons.Prefs
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo

class IntroNameFragment: SlideFragment() {

    private val nameEntry: TextInputEditText by bind(R.id.introNameNameEntry)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_intro_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        nameEntry.setOnKeyListener { _, actionId, _ ->

            return@setOnKeyListener if (actionId == EditorInfo.IME_ACTION_SEND) {
                nameEntry.clearFocus()
                Prefs.UserPref.userName = nameEntry.text.toString()
                true
            } else false

        }

    }

    override fun canMoveFurther() = nameEntry.text.isNotBlank()

    override fun backgroundColor() = R.color.intro_yellow

    override fun buttonsColor() = R.color.intro_yellow_dark

}

