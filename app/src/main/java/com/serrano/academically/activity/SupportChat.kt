package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.ChatField
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Utils
import com.serrano.academically.viewmodel.SupportChatViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun SupportChat(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    topicId: Int,
    supportChatViewModel: SupportChatViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        supportChatViewModel.getData(context)
    }

    val user by supportChatViewModel.drawerData.collectAsState()
    val process by supportChatViewModel.processState.collectAsState()
    val isRefreshLoading by supportChatViewModel.isRefreshLoading.collectAsState()
    val chats by supportChatViewModel.chats.collectAsState()
    val message by supportChatViewModel.message.collectAsState()
    val hasSentMessage by supportChatViewModel.hasSentMessage.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { supportChatViewModel.refreshData(context) }

    val initialMessages = listOf(
        "Please provide enough information and proof about the user that do inappropriate things",
        "Tell me what, where, how the problem occur.",
        "What you don't know, ask me question.",
        "Please provide enough information about the data fault, for example in session, provide the module, course, tutor/student, schedule, etc."
    )

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "SUPPORT CHAT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "SUPPORT CHAT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "SUPPORT CHAT",
                navController = navController,
                context = context,
                selected = "Support"
            ) { values ->
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = onRefresh,
                        refreshTriggerDistance = 50.dp
                    ) {
                        LazyColumn {
                            items(items = chats) {
                                Column(modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                ) {
                                    if (it.fromId == 1) {
                                        Text(text = "AcademicAlly Developer", modifier = Modifier.padding(horizontal = 10.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(MaterialTheme.shapes.extraSmall)
                                            .background(if (it.toId == 1) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                                            .fillMaxWidth(0.8f)
                                            .padding(10.dp)
                                            .align(if (it.toId == 1) Alignment.End else Alignment.Start)
                                    ) {
                                        Text(
                                            text = it.message,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = if (it.toId == 1) TextAlign.Right else TextAlign.Unspecified
                                        )
                                    }

                                    val date = Utils.convertToDate(it.date)
                                    Text(
                                        text = "${Utils.formatDate(date)} ${Utils.toMilitaryTime(date.hour, date.minute)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                        textAlign = if (it.toId == 1) TextAlign.Right else TextAlign.Unspecified
                                    )
                                }
                            }
                            item {
                                if (!hasSentMessage) {
                                    Column(modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth()
                                    ) {
                                        Text(text = "AcademicAlly Developer", modifier = Modifier.padding(horizontal = 10.dp))

                                        Box(
                                            modifier = Modifier
                                                .clip(MaterialTheme.shapes.extraSmall)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .fillMaxWidth(0.8f)
                                                .padding(10.dp)
                                                .align(Alignment.Start)
                                        ) {
                                            Text(
                                                text = initialMessages[topicId],
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Unspecified
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                ChatField(
                                    inputName = "Send Message",
                                    input = message,
                                    onInputChange = { supportChatViewModel.updateMessage(it) },
                                    onMessageSend = { supportChatViewModel.sendSupportMessage(it, user.id, context) },
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}