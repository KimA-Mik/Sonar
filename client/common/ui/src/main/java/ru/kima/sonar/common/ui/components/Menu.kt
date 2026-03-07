package ru.kima.sonar.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.ImmutableList
import ru.kima.sonar.common.ui.util.CommonDrawables

@Immutable
data class SonarMenuItem<T>(
    val title: Int,
    val onClick: (T) -> Unit,
    val leadingIcon: Int? = null,
    val trailingIcon: Int? = null
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> SonarMenu(
    input: T,
    items: ImmutableList<SonarMenuItem<T>>,
    modifier: Modifier = Modifier,
    buttonIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(CommonDrawables.more_vert_24px),
            contentDescription = null
        )
    }
) = Box(
    modifier = modifier
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        buttonIcon()
    }

    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(1, items.size)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(stringResource(item.title))
                    },
                    onClick = { item.onClick(input) },
                    leadingIcon = item.leadingIcon?.let {
                        {
                            Icon(
                                painter = painterResource(it),
                                contentDescription = null
                            )
                        }
                    },
                    trailingIcon = item.trailingIcon?.let {
                        {
                            Icon(
                                painter = painterResource(it),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}