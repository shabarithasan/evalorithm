package com.evalorithm.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.R
import com.evalorithm.data.local.TokenManager
import com.evalorithm.databinding.ActivityStudentFeedbackBinding
import com.evalorithm.databinding.DialogSubmitFeedbackBinding
import com.evalorithm.ui.adapter.FeedbackAdapter
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.OBEViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentFeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentFeedbackBinding
    private val viewModel: OBEViewModel by viewModels()
    private lateinit var feedbackAdapter: FeedbackAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        observeFeedback()

        loadFeedback()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Feedback"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupViewPager() {
        val tabTitles = listOf("Submit Feedback", "My Feedback")
        val fragments = listOf(SubmitFeedbackFragment(), MyFeedbackFragment())

        val adapter = FeedbackPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun loadFeedback() {
        lifecycleScope.launch {
            val userId = tokenManager.getUserId().first()
            viewModel.loadFeedback(userId)
        }
    }

    private fun observeFeedback() {
        viewModel.feedback.observe(this) { resource ->
            if (resource is Resource.Success) {
                val feedback = resource.data ?: emptyList()
                feedbackAdapter.submitList(feedback)
            }
        }
    }

    private inner class FeedbackPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {
        override fun getItemCount() = fragments.size
        override fun createFragment(position: Int) = fragments[position]
    }

    class SubmitFeedbackFragment : Fragment() {
        private var _binding: DialogSubmitFeedbackBinding? = null
        private val binding get() = _binding!!
        private val viewModel: OBEViewModel by viewModels()

        override fun onCreateView(inflater: LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = DialogSubmitFeedbackBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val types = listOf("Faculty", "Course", "Institution")
            val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
            binding.spinnerType.setAdapter(typeAdapter)
            binding.spinnerType.setText(types[0], false)

            binding.btnSubmit.setOnClickListener {
                val type = binding.spinnerType.text.toString()
                val rating = binding.ratingBar.rating.toInt()
                val comment = binding.etComment.text.toString().trim()
                val suggestions = binding.etSuggestions.text.toString().trim()

                if (comment.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (rating == 0) {
                    Toast.makeText(requireContext(), "Please select a rating", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.submitFeedback(type, null, null, rating, comment)
                Toast.makeText(requireContext(), "Feedback submitted", Toast.LENGTH_SHORT).show()
                binding.etComment.text?.clear()
                binding.etSuggestions.text?.clear()
                binding.ratingBar.rating = 0f
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    class MyFeedbackFragment : Fragment() {
        private var _binding: com.evalorithm.databinding.ActivityStudentCertificatesBinding? = null
        private val viewModel: OBEViewModel by viewModels()
        private lateinit var feedbackAdapter: FeedbackAdapter

        override fun onCreateView(inflater: LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = com.evalorithm.databinding.ActivityStudentCertificatesBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            feedbackAdapter = FeedbackAdapter()
            binding.rvCertificates.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCertificates.adapter = feedbackAdapter

            viewModel.feedback.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val list = resource.data ?: emptyList()
                        if (list.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvCertificates.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvCertificates.visibility = View.VISIBLE
                            feedbackAdapter.submitList(list)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}
