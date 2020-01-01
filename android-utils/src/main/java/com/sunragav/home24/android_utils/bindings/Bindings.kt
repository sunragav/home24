package com.sunragav.home24.android_utils.bindings

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sunragav.home24.android_utils.R


@BindingConversion
fun convertBooleanToVisibility(visible: Boolean): Int {
    return if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("layout_height")
fun setLayoutHeight(view: View, height: Float) {
    val layoutParams = view.layoutParams
    layoutParams.height = height.toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("layout_width")
fun setLayoutWidth(view: View, width: Float) {
    val layoutParams = view.layoutParams
    layoutParams.width = width.toInt()
    view.layoutParams = layoutParams
}

@BindingConversion
fun convertStringToText(field: ObservableField<String>): String {
    val str = field.get()
    return if (str.isNullOrBlank()) "" else str
}

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView?, field: ObservableField<String>?) {
    setImageUrl(view, field, 600, 400)
}


@BindingAdapter("bigImageUrl")
fun setBigImageUrl(view: ImageView?, field: ObservableField<String>?) {
    setImageUrl(
        view,
        field,
        1100,
        1500
    )

}

fun setImageUrl(view: ImageView?, field: ObservableField<String>?, width: Int, height: Int) {

    val url = field?.get()
    if (!url.isNullOrBlank() && view != null) {
        Glide.with(view.context)
            .load(url).apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .dontTransform()
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .override(width, height)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .sizeMultiplier(0.5f)
                    .placeholder(R.mipmap.ic_launcher)
            )
            .into(view)
    }

}