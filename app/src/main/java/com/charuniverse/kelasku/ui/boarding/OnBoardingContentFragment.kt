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
        R.drawable.img_tired,
        R.drawable.img_shout,
        R.drawable.img_tired,
    )

    private val onBoardingTitle = listOf(
        "Males ngebuka e-learning buat ngecek tugas?",
        "Pengumuman di grup kelas sering ketutupan?",
        "Sering kelupaan tugas yang gaada di e-learning?",
    )

    private val onBoardingDescription = listOf(
        "Aplikasi ini bakalan ngirimin kamu notifikasi setiap ada tugas baru",
        "Kamu bisa ngirim pemberitahuan ke temen sekelas pake aplikasi ini",
        "Tenang, kamu bisa nambahin tugas yang ga ke daftar. Temen sekelasmu bakalan dapet notifikasi juga loh",
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)

        ivOnBoardingContent.setImageResource(onBoardingImages[position])
        tvOnBoardingTitle.text = onBoardingTitle[position]
        tvOnBoardingDescription.text = onBoardingDescription[position]
    }

}