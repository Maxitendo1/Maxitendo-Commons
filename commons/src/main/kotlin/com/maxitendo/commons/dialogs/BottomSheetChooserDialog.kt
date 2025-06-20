package com.maxitendo.commons.dialogs

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.maxitendo.commons.R
import com.maxitendo.commons.adapters.setupSimpleListItem
import com.maxitendo.commons.compose.alert_dialog.dialogContainerColor
import com.maxitendo.commons.compose.alert_dialog.dialogTextColor
import com.maxitendo.commons.compose.bottom_sheet.BottomSheetColumnDialogSurface
import com.maxitendo.commons.compose.bottom_sheet.BottomSheetDialogState
import com.maxitendo.commons.compose.bottom_sheet.BottomSheetSpacerEdgeToEdge
import com.maxitendo.commons.compose.bottom_sheet.rememberBottomSheetDialogState
import com.maxitendo.commons.compose.extensions.MyDevices
import com.maxitendo.commons.compose.theme.AppThemeSurface
import com.maxitendo.commons.compose.theme.SimpleTheme
import com.maxitendo.commons.databinding.ItemSimpleListBinding
import com.maxitendo.commons.fragments.BaseBottomSheetDialogFragment
import com.maxitendo.commons.models.SimpleListItem

open class BottomSheetChooserDialog(collection: Boolean = false) : BaseBottomSheetDialogFragment() {

    val collection = collection
    var onItemClick: ((SimpleListItem) -> Unit)? = null

    override fun setupContentView(parent: ViewGroup) {
        val listItems = arguments?.getParcelableArray(ITEMS) as Array<SimpleListItem>
        listItems.forEach { item ->
            val view = ItemSimpleListBinding.inflate(layoutInflater, parent, false)
            setupSimpleListItem(view, item, collection) {
                onItemClick?.invoke(it)
                dismiss()
            }
            parent.addView(view.root)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onItemClick = null
    }

    companion object {
        private const val TAG = "BottomSheetChooserDialog"
        private const val ITEMS = "data"

        fun createChooser(
            fragmentManager: FragmentManager,
            title: Int?,
            items: Array<SimpleListItem>,
            collection: Boolean = false,
            callback: (SimpleListItem) -> Unit
        ): BottomSheetChooserDialog {
            val extras = Bundle().apply {
                if (title != null) {
                    putInt(BOTTOM_SHEET_TITLE, title)
                }
                putParcelableArray(ITEMS, items)
            }
            return BottomSheetChooserDialog(collection).apply {
                arguments = extras
                onItemClick = callback
                show(fragmentManager, TAG)
            }
        }
    }
}

@Composable
fun ChooserBottomSheetDialog(
    bottomSheetDialogState: BottomSheetDialogState,
    items: ImmutableList<SimpleListItem>,
    modifier: Modifier = Modifier,
    onItemClicked: (SimpleListItem) -> Unit
) {
    BottomSheetColumnDialogSurface(modifier) {
        Text(
            text = stringResource(id = R.string.please_select_destination),
            color = dialogTextColor,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(SimpleTheme.dimens.padding.extraLarge)
                .padding(top = SimpleTheme.dimens.padding.large)
        )
        for (item in items) {
            val color = if (item.selected) SimpleTheme.colorScheme.primary else SimpleTheme.colorScheme.onSurface
            val text = if (item.textRes != null) stringResource(id = item.textRes) else item.text ?: ""
            ListItem(
                modifier = Modifier
                    .clickable {
                        onItemClicked(item)
                        bottomSheetDialogState.close()
                    },
                headlineContent = {
                    Text(text, color = color)
                },
                leadingContent = {
                    if (item.imageRes != null) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = text,
                            colorFilter = ColorFilter.tint(color)
                        )
                    }
                },
                trailingContent = {
                    if (item.selected) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_check_circle_vector),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color)
                        )
                    }
                },
                colors = ListItemDefaults.colors(containerColor = dialogContainerColor)
            )
        }
        BottomSheetSpacerEdgeToEdge()
    }
}

@MyDevices
@Composable
private fun ChooserBottomSheetDialogPreview() {
    AppThemeSurface {
        val list = remember {
            listOf(
                SimpleListItem(1, textRes = R.string.record_video, imageRes = R.drawable.ic_camera_vector),
                SimpleListItem(2, textRes = R.string.record_audio, imageRes = R.drawable.ic_microphone_vector, selected = true),
                SimpleListItem(4, textRes = R.string.choose_contact, imageRes = R.drawable.ic_add_person_vector)
            ).toImmutableList()
        }
        ChooserBottomSheetDialog(bottomSheetDialogState = rememberBottomSheetDialogState(), items = list, onItemClicked = {})
    }
}
