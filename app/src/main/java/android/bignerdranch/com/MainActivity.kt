package android.bignerdranch.com

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

private const val CHARACTER_DATA_KEY = "CHARACTER_DATA_KEY"

class MainActivity : AppCompatActivity() {
    private var characterData = CharacterGenerator.generate()
    lateinit var nameTextView: TextView
    lateinit var raceTextView: TextView
    lateinit var dexterityTextView: TextView
    lateinit var wisdomTextView: TextView
    lateinit var strengthTextView: TextView
    lateinit var generateButton: Button

    private var Bundle.characterData
        get() = serializable<CharacterGenerator.CharacterData>(CHARACTER_DATA_KEY)
        set(value) = putSerializable(CHARACTER_DATA_KEY, value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Build.VERSION_CODES.TIRAMISU
        characterData = savedInstanceState?.characterData ?: CharacterGenerator.generate()

        nameTextView = findViewById(R.id.nameTextView)
        raceTextView = findViewById(R.id.raceTextView)
        dexterityTextView = findViewById(R.id.dexterityTextView)
        wisdomTextView = findViewById(R.id.wisdomTextView)
        strengthTextView = findViewById(R.id.strengthTextView)
        generateButton = findViewById(R.id.generateButton)

        generateButton.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                characterData = withContext(IO) { fetchCharacterData() }
            }
            displayCharacterData()
        }

        displayCharacterData()
    }

    private fun displayCharacterData() {
        characterData.run {
            nameTextView.text = name
            raceTextView.text = race
            dexterityTextView.text = dex
            wisdomTextView.text = wis
            strengthTextView.text = str
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.characterData = characterData
    }

    private inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }
}