package ru.kima.sonar.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
data class SonarListMenuItem<T>(
    val title: Int,
    val onClick: (T) -> Unit,
    val leadingIcon: Int? = null,
    val trailingIcon: Int? = null
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> SonarListMenu(
    input: T,
    items: ImmutableList<SonarListMenuItem<T>>,
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

sealed interface SonarDropdownMenuItem {
    val title: Int
    val leadingIcon: Int?
    val trailingIcon: Int?

    @Immutable
    data class SimpleItem(
        override val title: Int,
        override val leadingIcon: Int? = null,
        override val trailingIcon: Int? = null,
        val onClick: () -> Unit,
    ) : SonarDropdownMenuItem

    @Immutable
    data class ItemsGroup(
        override val title: Int,
        val children: ImmutableList<SonarDropdownMenuItem>,
        override val leadingIcon: Int? = null,
        override val trailingIcon: Int? = null
    ) : SonarDropdownMenuItem
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SonarDropdownMenu(
    expanded: Boolean,
    items: ImmutableList<SonarDropdownMenuItem>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) = DropdownMenuPopup(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    modifier = modifier
) {
    RecursiveSonarMenuGroup(items)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecursiveSonarMenuGroup(
    items: ImmutableList<SonarDropdownMenuItem>,
    modifier: Modifier = Modifier,
    index: Int = 0,
    itemContentPadding: PaddingValues = ExposedDropdownMenuDefaults.ItemContentPadding
) {
    DropdownMenuGroup(
        shapes = MenuDefaults.groupShape(index, items.size),
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            when (item) {
                is SonarDropdownMenuItem.SimpleItem -> DropdownMenuItem(
                    text = {
                        Text(stringResource(item.title))
                    },
                    onClick = { item.onClick() },
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
                    },
                    contentPadding = itemContentPadding
                )

                is SonarDropdownMenuItem.ItemsGroup -> {
                    var expanded by remember { mutableStateOf(false) }
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(item.title))
                        },
                        onClick = { expanded = true },
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
                        },
                        contentPadding = itemContentPadding
                    )

                    if (expanded) {
                        RecursiveSonarMenuGroup(item.children, index = index + 1)
                    }
                }
            }
        }
    }
}