package com.keysersoze.todotasks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    private lateinit var feedbackTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedbackTextView = view.findViewById(R.id.feedbackTextView)
        feedbackTextView.setOnClickListener {
            sendFeedbackEmail()
        }
    }

    private fun sendFeedbackEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:kskeysersoze23@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Feedback for Your App")
        }
        startActivity(emailIntent)
    }
}

