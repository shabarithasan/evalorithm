package com.evalorithm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evalorithm.data.model.Certificate
import com.evalorithm.databinding.ItemCertificateBinding

class CertificateAdapter(
    private val onVerifyClick: (Certificate) -> Unit
) : ListAdapter<Certificate, CertificateAdapter.CertificateViewHolder>(CertificateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val binding = ItemCertificateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CertificateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CertificateViewHolder(private val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(certificate: Certificate) {
            binding.tvCertType.text = certificate.certificateType
            binding.tvCertNumber.text = "Cert #: ${certificate.certificateNumber}"
            binding.tvStudentName.text = certificate.studentName
            binding.tvRegNumber.text = certificate.registerNumber
            binding.tvSubject.text = certificate.subjectName ?: ""
            binding.tvIssuedDate.text = "Issued: ${certificate.issuedDate}"
            binding.tvQrCode.text = certificate.qrCode

            binding.btnVerify.setOnClickListener { onVerifyClick(certificate) }
        }
    }

    class CertificateDiffCallback : DiffUtil.ItemCallback<Certificate>() {
        override fun areItemsTheSame(oldItem: Certificate, newItem: Certificate) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Certificate, newItem: Certificate) = oldItem == newItem
    }
}
