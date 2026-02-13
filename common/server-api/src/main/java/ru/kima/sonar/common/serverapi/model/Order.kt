package ru.kima.sonar.common.serverapi.model

import java.math.BigDecimal

/**Массив предложений/спроса.*/
data class Order(
    /**Цена за 1 инструмент. Чтобы получить стоимость лота, нужно умножить на лотность инструмента*/
    val price: BigDecimal,
    /**Количество в лотах.*/
    val quantity: Long
)