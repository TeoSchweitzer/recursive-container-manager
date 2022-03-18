package com.example.recursivecontainermanager.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.recursivecontainermanager.databinding.MainMenuFragmentBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel


open class MainMenuFragment: DialogFragment() {
    private var _binding: MainMenuFragmentBinding? = null
    private val b get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = MainMenuFragmentBinding.inflate(layoutInflater, null, false)

        b.showSignInButton.setOnClickListener { chooseTab(1) }
        b.showSignUpButton.setOnClickListener { chooseTab(2) }
        b.showAddressButton.setOnClickListener { chooseTab(3) }

        return AlertDialog.Builder(requireActivity())
            .setView(b.root)
            .setTitle("Main Settings")
            .setPositiveButton("Confirm",  ::confirmMenu as DialogInterface.OnClickListener)
            .create()
    }

    fun chooseTab(tab: Int) {
        when (tab) {
            1 -> showViews(b.usernameField, b.passwordField)
            2 -> showViews(b.usernameField, b.passwordField, b.passwordRepeatField)
            3 -> showViews(b.serverAddressField)
        }
    }

    private fun showViews(vararg views: View){
        b.root.allViews.forEach {
            if (it.id != View.NO_ID && it.parent == b.root) {
                if (!views.contains(it)) it.visibility = View.GONE
                else it.visibility = View.VISIBLE
            }
        }
    }

    private fun confirmMenu() {
        if (b.serverAddressField.isVisible) {
            val done = viewModel.newServerAddress(b.serverAddressField.text.toString())
            if (done) Toast.makeText(context,"New server address was set.", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context,"New server address is not valid.", Toast.LENGTH_SHORT).show()
        }
        else if (b.passwordRepeatField.isVisible) {
            if (b.passwordField.text.toString()!=b.passwordRepeatField.text.toString()) {
                Toast.makeText(context, "Repeated passwords must match.", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.createAccount(b.usernameField.text.toString(), b.passwordField.text.toString())
        }
        else if (b.usernameField.isVisible) {
            viewModel.authenticate(b.usernameField.text.toString(), b.passwordField.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}