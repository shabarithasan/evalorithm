package com.evalorithm.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.evalorithm.data.model.Attainment
import com.evalorithm.data.model.CourseOutcome
import com.evalorithm.databinding.ActivityObeAssessmentBinding
import com.evalorithm.util.Resource
import com.evalorithm.viewmodel.OBEViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OBEAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObeAssessmentBinding
    private val viewModel: OBEViewModel by lazy { ViewModelProvider(this)[OBEViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObeAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "OBE Assessment"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupViewPager() {
        val tabTitles = listOf("Course Outcomes", "Attainment")
        val fragments = listOf(COsFragment(), AttainmentFragment())

        val adapter = OBEFragmentPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private inner class OBEFragmentPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : androidx.viewpager2.adapter.FragmentStateAdapter(activity) {
        override fun getItemCount() = fragments.size
        override fun createFragment(position: Int) = fragments[position]
    }

    class COsFragment : Fragment() {
        private var _binding: com.evalorithm.databinding.ActivityStudentCertificatesBinding? = null
        private val binding get() = _binding!!
        private val viewModel: OBEViewModel by lazy { ViewModelProvider(requireActivity())[OBEViewModel::class.java] }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = com.evalorithm.databinding.ActivityStudentCertificatesBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.tvNoCertificates.text = "No Course Outcomes"

            viewModel.loadCOs(1L)

            viewModel.cos.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val cos = resource.data ?: emptyList()
                        if (cos.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvCertificates.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvCertificates.visibility = View.VISIBLE
                            displayCOs(cos)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun displayCOs(cos: List<CourseOutcome>) {
            val layout = binding.rvCertificates.parent as? ViewGroup
            binding.rvCertificates.visibility = View.GONE

            val container = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(16, 8, 16, 16)
            }

            cos.forEach { co ->
                val card = com.google.android.material.card.MaterialCardView(requireContext()).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = 8.dpToPx() }
                    radius = 12f.dpToPx()
                    cardElevation = 2f.dpToPx()
                }

                val ll = android.widget.LinearLayout(requireContext()).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(16.dpToPx().toInt(), 16.dpToPx().toInt(), 16.dpToPx().toInt(), 16.dpToPx().toInt())
                }

                val codeTv = android.widget.TextView(requireContext()).apply {
                    text = co.code
                    textSize = 16f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                ll.addView(codeTv)

                co.description?.let {
                    val descTv = android.widget.TextView(requireContext()).apply {
                        text = it
                        textSize = 13f
                        setTextColor(android.graphics.Color.GRAY)
                        setPadding(0, 4, 0, 0)
                    }
                    ll.addView(descTv)
                }

                val infoTv = android.widget.TextView(requireContext()).apply {
                    text = "Subject: ${co.subjectName ?: "N/A"} | Bloom's: ${co.bloomsLevel ?: "N/A"} | Mappings: ${co.mappingCount}"
                    textSize = 11f
                    setTextColor(android.graphics.Color.GRAY)
                    setPadding(0, 8, 0, 0)
                }
                ll.addView(infoTv)

                card.addView(ll)
                container.addView(card)
            }

            (binding.rvCertificates.parent as? ViewGroup)?.addView(container)
        }

        private fun Int.dpToPx(): Float = this * resources.displayMetrics.density
        private fun Float.dpToPx(): Float = this * resources.displayMetrics.density

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    class AttainmentFragment : Fragment() {
        private var _binding: com.evalorithm.databinding.ActivityStudentCertificatesBinding? = null
        private val binding get() = _binding!!
        private val viewModel: OBEViewModel by lazy { ViewModelProvider(requireActivity())[OBEViewModel::class.java] }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = com.evalorithm.databinding.ActivityStudentCertificatesBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.tvNoCertificates.text = "No Attainment Data"

            viewModel.loadAttainment(1L, 1L)

            viewModel.attainment.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val data = resource.data ?: emptyList()
                        if (data.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvCertificates.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvCertificates.visibility = View.VISIBLE
                            displayAttainment(data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun displayAttainment(data: List<Attainment>) {
            binding.rvCertificates.visibility = View.GONE

            val container = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(16, 8, 16, 16)
            }

            data.forEach { att ->
                val card = com.google.android.material.card.MaterialCardView(requireContext()).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = 8.dpToPx() }
                    radius = 12f.dpToPx()
                    cardElevation = 2f.dpToPx()
                }

                val ll = android.widget.LinearLayout(requireContext()).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(16.dpToPx().toInt(), 16.dpToPx().toInt(), 16.dpToPx().toInt(), 16.dpToPx().toInt())
                }

                val codeTv = android.widget.TextView(requireContext()).apply {
                    text = "${att.coCode} - ${att.coDescription ?: ""}"
                    textSize = 14f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                ll.addView(codeTv)

                val progress = android.widget.ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
                    max = 100
                    progress = (att.actualAttainment * 100).toInt()
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        16.dpToPx().toInt()
                    ).apply { topMargin = 8.dpToPx().toInt() }
                }
                ll.addView(progress)

                val statsTv = android.widget.TextView(requireContext()).apply {
                    text = "Target: ${(att.targetAttainment * 100).toInt()}% | Actual: ${(att.actualAttainment * 100).toInt()}% | Direct: ${(att.directAttainment * 100).toInt()}% | Indirect: ${(att.indirectAttainment * 100).toInt()}%"
                    textSize = 11f
                    setTextColor(android.graphics.Color.GRAY)
                    setPadding(0, 4, 0, 0)
                }
                ll.addView(statsTv)

                val achievedTv = android.widget.TextView(requireContext()).apply {
                    text = if (att.isAchieved) "Achieved" else "Not Achieved"
                    textSize = 12f
                    setTextColor(if (att.isAchieved) android.graphics.Color.GREEN else android.graphics.Color.RED)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 4, 0, 0)
                }
                ll.addView(achievedTv)

                card.addView(ll)
                container.addView(card)
            }

            (binding.rvCertificates.parent as? ViewGroup)?.addView(container)
        }

        private fun Int.dpToPx(): Float = this * resources.displayMetrics.density
        private fun Float.dpToPx(): Float = this * resources.displayMetrics.density

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}
