package ru.kima.sonar.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


data class NavBarItem(
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    @StringRes val description: Int
)
