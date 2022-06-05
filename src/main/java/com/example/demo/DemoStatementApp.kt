package com.example.demo

import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

open class DemoStatementApp {

    companion object {
        fun getOperations() : List<Operation> {
            val in1  = PayPointDto("12345678", Direction.TO)
            val in2  = PayPointDto("12345687", Direction.TO)
            val out1 = PayPointDto("12345678", Direction.FROM)
            val out2 = PayPointDto("12345687", Direction.FROM)
            return mutableListOf<Operation>().apply {
                repeat(10) {
                    val income = Random.nextBoolean();
                    this.add(Operation(LocalDateTime.now(), "Название $it", "Описание $it",
                        BigDecimal.valueOf(Random(it).nextInt().toLong(), 2),
                        if (income) in1 else in2,
                        if (income) out2 else out1))
                }
            }.toList()
        }
    }

}