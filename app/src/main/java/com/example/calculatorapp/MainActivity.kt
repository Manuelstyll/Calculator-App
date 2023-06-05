package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.calculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun numberAction(view:View){
        if (view is Button){
            if (view.text == "."){
                if (canAddDecimal)
                    binding.workingstv.append(view.text)
                canAddDecimal = false
            }
            else
                binding.workingstv.append(view.text)

            canAddOperation = true
        }
    }
    fun operatorAction(view:View){
        if (view is Button && canAddOperation){
            binding.workingstv.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view:View){
        binding.workingstv.text=""
        binding.resultstv.text=""
    }
    fun backSpaceAction(view:View){
        val length = binding.workingstv.length()
        if (length > 0)
            binding.workingstv.text =binding.workingstv.text.subSequence(0, length -1)
    }

    private fun calculateResults(): String{
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""
        val timeDivision = timeDivisionCalculate(digitsOperators)
        if (timeDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timeDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices){
            if (passedList[i] is Char && i !=passedList.lastIndex){
                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }
        return result
    }

    private fun timeDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/'))
        {
            list = calcTimeDiv(list)
        }
        return list
    }

    private fun calcTimeDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()

        var restartIndex = passedList.size

        for (i in passedList.indices){
            if (passedList[i] is Char && i !=passedList.lastIndex && i < restartIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when(operator){
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else ->{
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    fun equalsAction(view:View){
        binding.resultstv.text = calculateResults()
    }

    private fun digitsOperators():MutableList<Any>{
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.workingstv.text){
            if (character.isDigit() || character == '.')
                currentDigit += character
            else{
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        if (currentDigit != "")
            list.add(currentDigit.toFloat())
        return list

    }
}