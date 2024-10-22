package com.example.scramble

import android.content.ClipData
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.scramble.R
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var feedbackTextView: TextView
    private lateinit var checkButton: Button
    private lateinit var wordSet: Set<String> // For dictionary

    private lateinit var currentWord: String
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreTextView = findViewById(R.id.score)
        feedbackTextView = findViewById(R.id.feedback)
        checkButton = findViewById(R.id.checkButton)

        // Load dictionary from assets
        wordSet = loadDictionary()

        // Set random word and display scrambled letters
        currentWord = wordSet.random().uppercase() // Pick a random word from dictionary
        val scrambledWord = currentWord.toCharArray().toList().shuffled().joinToString("")

        setupCharacterDrag(scrambledWord)

        // Setup drop functionality for the 7 boxes
        setupDropTargets()

        // Button to check the word
        checkButton.setOnClickListener {
            checkWord()
        }
    }

    private fun loadDictionary(): Set<String> {
        val wordSet = mutableSetOf<String>()
        val inputStream = assets.open("dictionary.txt") // Ensure the file is in 'assets' folder
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            line?.let { wordSet.add(it.trim().uppercase()) }
        }
        reader.close()
        return wordSet
    }

    // Setup drag listeners for character TextViews
    private fun setupCharacterDrag(scrambledWord: String) {
        val charViews = listOf(
            findViewById<TextView>(R.id.char1),
            findViewById<TextView>(R.id.char2),
            findViewById<TextView>(R.id.char3),
            findViewById<TextView>(R.id.char4),
            findViewById<TextView>(R.id.char5),
            findViewById<TextView>(R.id.char6),
            findViewById<TextView>(R.id.char7)
        )

        for (i in scrambledWord.indices) {
            charViews[i].text = scrambledWord[i].toString()
            charViews[i].setOnLongClickListener { view ->
                val dragData = ClipData.newPlainText("char", charViews[i].text)
                val dragShadow = View.DragShadowBuilder(view)
                view.startDragAndDrop(dragData, dragShadow, view, 0)
                true
            }
        }
    }

    // Setup drop targets (boxes) for dragged characters
    private fun setupDropTargets() {
        val boxes = listOf(
            findViewById<EditText>(R.id.box1),
            findViewById<EditText>(R.id.box2),
            findViewById<EditText>(R.id.box3),
            findViewById<EditText>(R.id.box4),
            findViewById<EditText>(R.id.box5),
            findViewById<EditText>(R.id.box6),
            findViewById<EditText>(R.id.box7)
        )

        for (box in boxes) {
            box.setOnDragListener { view, event -> handleDrop(view, event) }
        }
    }

    // Handle drop events
    private fun handleDrop(view: View, event: DragEvent): Boolean {
        val box = view as EditText
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> return true
            DragEvent.ACTION_DROP -> {
                val clipData = event.clipData
                val char = clipData.getItemAt(0).text.toString()
                box.setText(char)
                return true
            }
            else -> return false
        }
    }

    // Check if the boxes form a valid word
    private fun checkWord() {
        val boxes = listOf(
            findViewById<EditText>(R.id.box1),
            findViewById<EditText>(R.id.box2),
            findViewById<EditText>(R.id.box3),
            findViewById<EditText>(R.id.box4),
            findViewById<EditText>(R.id.box5),
            findViewById<EditText>(R.id.box6),
            findViewById<EditText>(R.id.box7)
        )

        val formedWord = boxes.joinToString("") { it.text.toString() }

        if (wordSet.contains(formedWord)) {
            feedbackTextView.text = "Correct!"
            score++
            scoreTextView.text = "Score: $score"
        } else {
            feedbackTextView.text = "Try Again!"
        }
    }
}