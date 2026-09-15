package com.cslineups.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cslineups.app.data.ImageStore
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Difficulty
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.LineupItem
import com.cslineups.app.model.UtilityType
import com.cslineups.app.ui.components.ChoiceRow
import com.cslineups.app.ui.components.glassBackdropBlur
import com.cslineups.app.ui.components.ImageSlot
import com.cslineups.app.ui.components.ListCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.components.TextEditDialog
import com.cslineups.app.ui.label
import kotlinx.coroutines.launch

private enum class EditableField { NAME, START, TARGET, STEPS, NOTES }

private enum class ImageSlotKind { POSITION, AIM, RESULT }

/**
 * 道具编辑页：所有改动立即写入本地存储（自动保存），不需要点"保存"。
 */
@Composable
fun LineupEditorScreen(
    item: LineupItem,
    strings: Strings,
    onUpdate: (LineupItem) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editing by remember { mutableStateOf<EditableField?>(null) }
    var pendingSlot by remember { mutableStateOf<ImageSlotKind?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        val slot = pendingSlot
        pendingSlot = null
        if (uri == null || slot == null) return@rememberLauncherForActivityResult

        scope.launch {
            val name = ImageStore.import(context, uri) ?: return@launch
            val previous = when (slot) {
                ImageSlotKind.POSITION -> item.positionImage
                ImageSlotKind.AIM -> item.aimImage
                ImageSlotKind.RESULT -> item.resultImage
            }
            onUpdate(
                when (slot) {
                    ImageSlotKind.POSITION -> item.copy(positionImage = name)
                    ImageSlotKind.AIM -> item.copy(aimImage = name)
                    ImageSlotKind.RESULT -> item.copy(resultImage = name)
                },
            )
            ImageStore.delete(context, previous)
        }
    }

    fun pick(slot: ImageSlotKind) {
        pendingSlot = slot
        pickImage.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }

    Box(Modifier.fillMaxSize()) {
    ScreenScaffold(
        title = strings.editItem,
        onBack = onBack,
        titleContent = {
            Column {
                Text(strings.editItem, style = MaterialTheme.typography.titleLarge)
                Text(
                    strings.autosaveHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        modifier = Modifier.glassBackdropBlur(editing != null),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(PagePadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SectionTitle(strings.overview)
            ListCard {
                EditableRow(
                    label = strings.name,
                    value = item.name,
                    placeholder = strings.emptyValue,
                    onClick = { editing = EditableField.NAME },
                )
            }

            Spacer(Modifier.height(12.dp))
            ChoiceRow(
                label = strings.type,
                options = UtilityType.entries.map { it.label(strings) },
                selectedIndex = UtilityType.entries.indexOf(item.type),
                onSelect = { onUpdate(item.copy(type = UtilityType.entries[it])) },
            )

            Spacer(Modifier.height(12.dp))
            ChoiceRow(
                label = strings.side,
                options = SIDES,
                selectedIndex = SIDES.indexOf(item.side).coerceAtLeast(0),
                onSelect = { onUpdate(item.copy(side = SIDES[it])) },
            )

            Spacer(Modifier.height(12.dp))
            ChoiceRow(
                label = strings.category,
                options = LineupCategory.entries.map { it.label(strings) },
                selectedIndex = LineupCategory.entries.indexOf(item.category),
                onSelect = { onUpdate(item.copy(category = LineupCategory.entries[it])) },
            )

            Spacer(Modifier.height(12.dp))
            ChoiceRow(
                label = strings.difficulty,
                options = Difficulty.entries.map { it.label(strings) },
                selectedIndex = Difficulty.entries.indexOf(item.difficulty),
                onSelect = { onUpdate(item.copy(difficulty = Difficulty.entries[it])) },
            )

            SectionTitle(strings.startArea)
            ListCard {
                EditableRow(
                    label = strings.startArea,
                    value = item.startArea,
                    placeholder = strings.emptyValue,
                    onClick = { editing = EditableField.START },
                )
                EditableRow(
                    label = strings.targetArea,
                    value = item.targetArea,
                    placeholder = strings.emptyValue,
                    onClick = { editing = EditableField.TARGET },
                )
            }

            SectionTitle(strings.throwSteps)
            ListCard {
                EditableRow(
                    label = strings.throwSteps,
                    value = item.throwSteps,
                    placeholder = strings.emptyValue,
                    onClick = { editing = EditableField.STEPS },
                )
            }

            SectionTitle(strings.notes)
            ListCard {
                EditableRow(
                    label = strings.notes,
                    value = item.notes,
                    placeholder = strings.emptyValue,
                    onClick = { editing = EditableField.NOTES },
                )
            }

            SectionTitle(strings.teachingImages)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ImageSlot(
                    label = strings.positionImage,
                    imageName = item.positionImage,
                    strings = strings,
                    onPick = { pick(ImageSlotKind.POSITION) },
                    onClear = {
                        onUpdate(item.copy(positionImage = null))
                        scope.launch { ImageStore.delete(context, item.positionImage) }
                    },
                )
                ImageSlot(
                    label = strings.aimImage,
                    imageName = item.aimImage,
                    strings = strings,
                    onPick = { pick(ImageSlotKind.AIM) },
                    onClear = {
                        onUpdate(item.copy(aimImage = null))
                        scope.launch { ImageStore.delete(context, item.aimImage) }
                    },
                )
                ImageSlot(
                    label = strings.resultImage,
                    imageName = item.resultImage,
                    strings = strings,
                    onPick = { pick(ImageSlotKind.RESULT) },
                    onClear = {
                        onUpdate(item.copy(resultImage = null))
                        scope.launch { ImageStore.delete(context, item.resultImage) }
                    },
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    editing?.let { field ->
        val (initial, multiline) = when (field) {
            EditableField.NAME -> item.name to false
            EditableField.START -> item.startArea to false
            EditableField.TARGET -> item.targetArea to false
            EditableField.STEPS -> item.throwSteps to true
            EditableField.NOTES -> item.notes to true
        }
        val label = when (field) {
            EditableField.NAME -> strings.itemName
            EditableField.START -> strings.startArea
            EditableField.TARGET -> strings.targetArea
            EditableField.STEPS -> strings.throwSteps
            EditableField.NOTES -> strings.notes
        }

        TextEditDialog(
            title = label,
            label = label,
            initialValue = initial,
            confirmText = strings.save,
            cancelText = strings.cancel,
            multiline = multiline,
            onConfirm = { updated ->
                onUpdate(
                    when (field) {
                        EditableField.NAME -> item.copy(name = updated.ifBlank { item.name })
                        EditableField.START -> item.copy(startArea = updated)
                        EditableField.TARGET -> item.copy(targetArea = updated)
                        EditableField.STEPS -> item.copy(throwSteps = updated)
                        EditableField.NOTES -> item.copy(notes = updated)
                    },
                )
                editing = null
            },
            onDismiss = { editing = null },
        )
    }
    }
}

private val SIDES = listOf("T", "CT")

@Composable
private fun EditableRow(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "›",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
