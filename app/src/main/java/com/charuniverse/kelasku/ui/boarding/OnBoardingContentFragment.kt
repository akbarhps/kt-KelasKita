package com.charuniverse.kelasku.ui.boarding

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.charuniverse.kelasku.R
import kotlinx.android.synthetic.main.fragment_on_boarding_content.*

class OnBoardingContentFragment : Fragment(R.layout.fragment_on_boarding_content) {

    companion object {
        private const val ARG_POSITION = "ARG_POSITION"

        fun getInstance(position: Int) = OnBoardingContentFragment().apply {
            arguments = bundleOf(ARG_POSITION to position)
        }
    }

    private val onBoardingImages = listOf(
        R.drawable.img_message,
        R.drawable.img_shout,
        R.drawable.img_delivery,
    )

    private val onBoardingTitle = listOf(
        "Males buka e-learning buat ngecek tugas?",
        "Pengumuman di grup kelas sering ketutupan?",
        "Sering kelupaan tugas yang gaada di e-learning?",
    )

    private val onBoardingDescription = listOf(
        "Tenang, kamu bakal dikirimin notifikasi setiap ada tugas baru",
        "Mudah, di aplikasi ini kamu bisa ngirim pemberitahuan ke temen sekelas",
        "Engga bakalan lagi kok, karena kamu juga bisa nambahin tugas yang ga ke daftar di e-learning"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)

        ivOnBoardingContent.setImageResource(onBoardingImages[position])
        tvOnBoardingTitle.text = onBoardingTitle[position]
        tvOnBoardingDescription.text = onBoardingDescription[position]
    }

}