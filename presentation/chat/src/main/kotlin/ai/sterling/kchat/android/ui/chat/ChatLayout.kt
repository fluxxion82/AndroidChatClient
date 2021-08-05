package ai.sterling.kchat.android.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import ai.sterling.kchat.R
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.model.ChatMessage.Companion.LOGIN
import ai.sterling.kchat.domain.chat.model.ChatMessage.Companion.LOGOUT
import ai.sterling.kchat.domain.chat.model.ChatMessage.Companion.MESSAGE
import ai.sterling.kchat.domain.chat.model.ChatMessage.Companion.REPLY
import ai.sterling.kchat.android.ui.base.compose.green_700
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ChatContent(navController: NavController, viewModel: ChatViewModel) {
    val messages = viewModel.messages.collectAsState()

    ChatContent(messages = messages.value, viewModel::onSendMessage)
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ChatContent(
    messages: List<ChatMessage>,
    sendMessage:(String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val (list, input) = createRefs()
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyVerticalGrid(
            cells = GridCells.Fixed(1),
            modifier = Modifier
                .background(color = Color.White)
                .constrainAs(list) {
                    bottom.linkTo(input.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            state = listState,
            contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 15.dp)
        ) {
            items(
                items = messages,
                itemContent = {
                    ChatItem(chatMessage = it)
                },
            )
            coroutineScope.launch {
                // Animate scroll to the first item
                listState.scrollToItem(index = messages.size + 2)
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .constrainAs(input) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            ChatMessageInput {
                coroutineScope.launch {
                    // Animate scroll to the first item
                    listState.scrollToItem(index = messages.size + 1)
                }
                sendMessage(it)
            }
        }
    }
}

@Composable
fun ChatItem(chatMessage: ChatMessage) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement =
        when (chatMessage.type) {
            MESSAGE -> Arrangement.End
            REPLY -> Arrangement.Start
            else -> Arrangement.Center
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (chatMessage.type) {
            MESSAGE -> Unit
            REPLY -> Image(
                painterResource(R.drawable.ic_profile),
                contentDescription = "${chatMessage.username}'s avatar"
            )
            LOGIN, LOGOUT -> Unit
        }

        Card(
            modifier = if (chatMessage.type == LOGIN || chatMessage.type == LOGOUT) {
                Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .border(3.dp, green_700)
            } else {
                Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            },
            elevation = 4.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
//            Image(
//                painter =
//                rememberDrawablePainter(
//                    ContextCompat.getDrawable(LocalContext.current, R.drawable.message_sent_2)
//                ),
//                contentDescription = "send background",
//                modifier = Modifier.height(IntrinsicSize.Min)
//            )

            when (chatMessage.type) {
                MESSAGE ->
                    Text(
                        text = chatMessage.toString(),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.Black,
                        style = typography.body2,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End
                    )
                REPLY -> {
//                    Image(
//                        painter =
//                        rememberDrawablePainter(
//                            ContextCompat.getDrawable(LocalContext.current, R.drawable.message_received_2)
//                        ),
//                        contentDescription = "send background",
//                        modifier = Modifier.height(IntrinsicSize.Min)
//                    )

                    Text(
                        text = chatMessage.toString(),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.Black,
                        style = typography.body2,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                }
                LOGIN, LOGOUT ->
                    Text(
                        text = chatMessage.toString(),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        color = Color.Black,
                        style = typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
            }
        }

        when (chatMessage.type) {
            MESSAGE -> Image(
                painterResource(R.drawable.ic_profile),
                contentDescription = "my avatar",
                modifier = Modifier.padding(5.dp)
            )
            REPLY -> Unit
            LOGIN, LOGOUT -> Unit
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun ChatMessageInput(
    sendMessage:(String) -> Unit
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        var messageInput by rememberSaveable { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            value = messageInput,
            onValueChange = {
                messageInput = it
            },
            label = { },
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions  = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    sendMessage(messageInput)
                }
            )
        )
        Button(
            onClick = {
                sendMessage(messageInput)
                messageInput = ""
                      },
               modifier = Modifier.fillMaxHeight()
        ) {
            Image(painter = painterResource(id = R.drawable.ic_36_send), contentDescription = "send background")
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Preview(name = "chat message list content")
@Composable
fun PreviewChatContent() {
    ChatContent(
        listOf(
            ChatMessage(
                1,
                "Me",
                MESSAGE,
                "Hello, this is a test message",
                Clock.System.now().toEpochMilliseconds()
            ),
            ChatMessage(
                1,
                "Tester",
                LOGIN,
                "foo",
                Clock.System.now().toEpochMilliseconds()
            ),
            ChatMessage(
                2,
                "Tester",
                REPLY,
                "Hello, this is a test reply message",
                Clock.System.now().toEpochMilliseconds()
            ),
            ChatMessage(
                1,
                "Tester",
                LOGOUT,
                "foo",
                Clock.System.now().toEpochMilliseconds()
            )
        ),
        {}
    )
}

@ExperimentalFoundationApi
@Preview(name = "chat message item me")
@Composable
fun PreviewMessageItem() {
    ChatItem(
        ChatMessage(
            1,
            "Me",
            MESSAGE,
            "Hello, this is a test message",
            Clock.System.now().toEpochMilliseconds()
        )
    )
}

@ExperimentalFoundationApi
@Preview(name = "chat message item reply")
@Composable
fun PreviewReplyMessage() {
    ChatItem(
        ChatMessage(
            2,
            "Tester",
            REPLY,
            "Hello, this is a test reply message",
            Clock.System.now().toEpochMilliseconds()
        )
    )
}

@ExperimentalFoundationApi
@Preview(name = "chat message item login")
@Composable
fun PreviewNeutralMessage() {
    ChatItem(
        ChatMessage(
            1,
            "Tester",
            LOGIN,
            "foo",
            Clock.System.now().toEpochMilliseconds()
        )
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Preview(name = "chat input layout")
@Composable
fun PreviewChatInput() {
    ChatMessageInput({})
}
