package h.Hchat.ui.miuix

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import h.Hchat.hooks.items.autoreply.AutoReplySettings
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle

/**
 * 好友请求处理页：不再自动同意好友申请，改为用户手动通过后自动追加备注日期。
 *
 * 单独放一个文件，避免继续把代码堆进 MiuixSettingsPageKt：
 * 那个文件已经是 2 MB 级别的巨型类，常量池逼近 JVM 上限，再加内容会直接
 * `ClassTooLargeException: Class too large: h/Hchat/ui/miuix/MiuixSettingsPageKt`。
 */
@Composable
internal fun AutoReplyManualFriendPage(
    context: Context,
    sp: SharedPreferences,
    onBack: () -> Unit
) {
    var remarkDateFormat by remember {
        mutableStateOf(
            sp.getString(
                AutoReplySettings.KEY_MANUAL_FRIEND_REMARK_DATE_FORMAT,
                AutoReplySettings.DEFAULT_MANUAL_FRIEND_REMARK_DATE_FORMAT
            ) ?: AutoReplySettings.DEFAULT_MANUAL_FRIEND_REMARK_DATE_FORMAT
        )
    }
    val scrollBehavior = MiuixScrollBehavior()
    PageScaffold(
        title = "好友请求处理",
        largeTitle = "好友请求处理",
        scrollBehavior = scrollBehavior,
        bottomBar = {
            BottomActionBar(
                primaryText = "保存",
                onPrimaryClick = {
                    sp.edit()
                        .putString(
                            AutoReplySettings.KEY_MANUAL_FRIEND_REMARK_DATE_FORMAT,
                            remarkDateFormat.trim().ifBlank {
                                AutoReplySettings.DEFAULT_MANUAL_FRIEND_REMARK_DATE_FORMAT
                            }
                        )
                        .apply()
                    Toast.makeText(context, "设置已保存", Toast.LENGTH_SHORT).show()
                },
                secondaryText = "返回",
                onSecondaryClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 84.dp
            )
        ) {
            item { SmallTitle(text = "好友请求") }
            item {
                SettingsCard {
                    SwitchRow(
                        sp,
                        AutoReplySettings.KEY_MANUAL_FRIEND_ENABLE,
                        "手动通过后自动备注",
                        "不会自动同意好友申请，需要你在微信里手动通过",
                        AutoReplySettings.DEFAULT_MANUAL_FRIEND_ENABLE
                    )
                    InsetDivider()
                    InputRow("备注日期格式", "例如 yyyy-MM-dd", remarkDateFormat) { remarkDateFormat = it }
                }
            }
            item { SmallTitle(modifier = Modifier.padding(top = 10.dp), text = "说明") }
            item {
                SettingsCard {
                    EmptyText("开启后不会替你自动点“通过”。你在微信里手动通过好友申请后，模块会把当天日期追加到该好友备注后面，例如“张三 2026-10-23”；已有备注会保留，日期已存在时不会重复追加。")
                }
            }
        }
    }
}
