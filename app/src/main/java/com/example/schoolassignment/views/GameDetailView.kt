package com.example.schoolassignment.views

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.schoolassignment.R
import com.example.schoolassignment.gameDTOs.MinimumSystemRequirements
import com.example.schoolassignment.viewModels.GameDetailViewModel
import com.example.schoolassignment.viewModels.GameOverviewViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun GameDetailView(mainNavController: NavHostController, gameId: String) {

    val gameDetailVM = viewModel<GameDetailViewModel>()
    val gameOverviewVM =  viewModel<GameOverviewViewModel>(LocalContext.current as ComponentActivity)

    gameDetailVM.getSpecificGame(gameId.toInt())

    if (gameDetailVM.specificGame.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
    } else {

        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
        )


        BottomSheetScaffold(
            topBar = {
                GameDetailTopBar(
                    mainNavController,
                    gameDetailVM,
                    gameOverviewVM,
                    scaffoldState
                )
            },
            sheetContent = { AddNoteDialog(scaffoldState, gameDetailVM) },
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetElevation = 10.dp,
            sheetBackgroundColor = Color(0xFFF5EFEF),
        ) {
            GameDetail(gameDetailVM, gameOverviewVM)

        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AddNoteDialog(scaffoldState: BottomSheetScaffoldState, gameDetailVM: GameDetailViewModel) {

    var note by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") }
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row {
            TextButton(onClick = {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.collapse()
                }
            }) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedButton(
                onClick = {
                    if(note.trim().isNotEmpty()){
                        gameDetailVM.addNote(note)
                    }
                    note = ""
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }, border = BorderStroke(1.dp, MaterialTheme.colors.primary)
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun GameDetail(gameDetailVM: GameDetailViewModel, gameOverviewVM: GameOverviewViewModel) {

    Column {
        AsyncImage(
            model = gameDetailVM.specificGame.value!!.thumbnail,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .border(
                    border = BorderStroke(width = 1.dp, Color.Black),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(3.dp)

        ) {
            items(count = gameDetailVM.specificGame.value!!.screenshots.size, itemContent = { index ->
                GameScreenshot(screenshotURL = gameDetailVM.specificGame.value!!.screenshots[index].image)
            })
        }
        if (gameOverviewVM.showFavouritesPage) {
            PersonalNotes(gameDetailVM)
        } else {
            SystemRequirements(sysReq = gameDetailVM.specificGame.value!!.minimum_system_requirements)
        }
    }
}

@Composable
fun PersonalNotes(gameDetailVM: GameDetailViewModel) {

    gameDetailVM.getSpecificGameNotes()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 2.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
            .border(
                border = BorderStroke(width = 1.dp, Color.Black),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(8.dp)
    ) {
        if (!gameDetailVM.isLoadingNotes.value) {
            val notes = gameDetailVM.specificGameNotes.value
            if (notes.isEmpty()) {
                Text(
                    text = "No personal notes!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn {
                    items(count = notes.size, itemContent = { index ->
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                gameDetailVM.removeNote(notes[index].id)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                                    contentDescription = null,
                                    tint = Color(0xFFDD6363)
                                )
                            }
                            Text(text = notes[index].text,
                                fontSize = 22.sp,
                                fontStyle = FontStyle.Italic)
                        }

                    })
                }
            }
        }
    }
}

@Composable
fun SystemRequirements(sysReq: MinimumSystemRequirements) {
    Text(
        text = "Minimum system requirements", fontSize = 20.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
            .border(
                border = BorderStroke(width = 1.dp, Color.Black),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(8.dp)
    ) {
        val sysReqsMap = hashMapOf(
            "Graphics:" to sysReq.graphics,
            "Memory:" to sysReq.memory,
            "Operating system:" to sysReq.os,
            "Processor:" to sysReq.processor,
            "Storage:" to sysReq.storage,
        )
        sysReqsMap.forEach { (key, value) ->
            Row {
                Text(
                    text = key,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = value ?: "No requirement mentioned")
            }
        }
    }
}

@Composable
fun GameScreenshot(screenshotURL: String) {
    Box(modifier = Modifier.padding(4.dp)) {
        AsyncImage(
            model = screenshotURL,
            contentDescription = null,
            modifier = Modifier.clip(RoundedCornerShape(5.dp))
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun GameDetailTopBar(
    mainNavController: NavHostController,
    gameDetailVM: GameDetailViewModel,
    gameOverviewVM: GameOverviewViewModel,
    bottomSheetState: BottomSheetScaffoldState
) {
    TopAppBar(
        backgroundColor = Color(0xFF363333),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            Box(
                modifier = Modifier
                    .weight(0.7f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        mainNavController.popBackStack()
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = null,
                        tint = Color(0xFFF3E8E8),
                        modifier = Modifier
                            .height(18.dp)
                            .width(18.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Back", fontSize = 17.sp, color = Color(0xFFF3E8E8),
                    )
                }
            }
            Text(
                text = gameDetailVM.specificGame.value!!.title,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFF3E8E8),
                modifier = Modifier.weight(1f),
            )
            if (gameOverviewVM.showFavouritesPage) {
                val coroutineScope = rememberCoroutineScope()
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(end = 5.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                if (bottomSheetState.bottomSheetState.isCollapsed) {
                                    bottomSheetState.bottomSheetState.expand()
                                }
                            }
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_add_24),
                            contentDescription = null,
                            tint = Color(0xFFF3E8E8),
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Note", fontSize = 17.sp, color = Color(0xFFF3E8E8),
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(0.7f))
            }
        }
    }
}