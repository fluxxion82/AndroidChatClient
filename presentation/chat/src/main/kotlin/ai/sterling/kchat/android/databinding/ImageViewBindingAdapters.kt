//package ai.sterling.kchat.android.databinding
//
//import android.view.View
//import android.widget.ImageView
//import androidx.core.content.ContextCompat.getDrawable
//import androidx.databinding.BindingAdapter
//
//
//@BindingAdapter("android:background")
//fun ImageView.bindImageBackground(image: String) {
//    visibility = View.GONE
//    val index = image.indexOf("img:")
//    if (index >= 0) {
//        val drawableName = image.substring(image.indexOf(":")+1, image.indexOf(".gif"))
//        if (!drawableName.isNullOrEmpty()) {
//            val resourceId = resources.getIdentifier(drawableName, "drawable",
//                                                     context.packageName
//            );
//            setImageDrawable(getDrawable(this.context, resourceId))
//            visibility = View.VISIBLE
//        }
//    }
//
//}
