package com.alterpat.budgettracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.alterpat.budgettracker.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Clear error when user starts typing
        binding.labelInput.doAfterTextChanged {
            if (!it.isNullOrEmpty()) binding.labelLayout.error = null
        }

        binding.amountInput.doAfterTextChanged {
            if (!it.isNullOrEmpty()) binding.amountLayout.error = null
        }

        // Add transaction button click
        binding.addTransactionBtn.setOnClickListener {
            val label = binding.labelInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()
            val amount = binding.amountInput.text.toString().toDoubleOrNull()

            if (label.isEmpty()) {
                binding.labelLayout.error = "Please enter a valid label"
                return@setOnClickListener
            }

            if (amount == null) {
                binding.amountLayout.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            val transaction = Transaction(0, label, amount, description)
            insertTransaction(transaction)
        }

        // Close button click
        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insertTransaction(transaction: Transaction) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "transactions"
        ).build()

        lifecycleScope.launch {
            db.transactionDao().insertAll(transaction)
            finish() // safely finishes after insert
        }
    }
}
