//package ai.sterling.kchat.android.databinding
//
//import androidx.databinding.BindingAdapter
//import androidx.databinding.InverseBindingAdapter
//import androidx.lifecycle.MutableLiveData
//import com.google.android.material.textfield.TextInputEditText
//
//@BindingAdapter("currentPort")
//fun setPort(editText: TextInputEditText, port: MutableLiveData<Int>) {
//    port.value?.let {
//        //don't forget to break possible infinite loops!
//        if (editText.text.toString() != port.value.toString()) {
//            editText.setText(port.value.toString())
//        }
//    }
//}
//
//@InverseBindingAdapter(attribute = "currentPort", event = "afterTextChanged")
//fun getPort(editText: TextInputEditText) = Integer.getInteger(editText.text.toString())
