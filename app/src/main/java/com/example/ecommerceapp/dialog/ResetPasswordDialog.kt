package com.example.ecommerceapp.dialog

import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


// to use it multiple fragments


fun Fragment.setUpBottomSheetDialog(
    onSendClick: (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext())
    val view = layoutInflater.inflate(R.layout.reset_password_dialog,null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val emailET = view.findViewById<EditText>(R.id.resetEmail)
    val buttonSend = view.findViewById<AppCompatButton>(R.id.sendResetBtn)
    val buttonCancel = view.findViewById<AppCompatButton>(R.id.cancelResetBtn)

    buttonSend.setOnClickListener {
        val email = emailET.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}