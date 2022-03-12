package com.example.schoolassignment.views

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import com.example.schoolassignment.gameDTOs.GameDetailDTO
import com.example.schoolassignment.viewModels.GameViewModel

@Composable
fun GameDetailView(mainNavController: NavHostController, gameId: String) {

    val gameVM = viewModel<GameViewModel>(LocalContext.current as ComponentActivity)
    gameVM.getSpecificGame(gameId.toInt())

    if (gameVM.isLoadingSpecificGame.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = { GameDetailTopBar(mainNavController, gameVM) }
        ) {
            GameDetail(gameVM.specificGame, gameVM.showFavouritesPage)
        }
    }
}

@Composable
fun GameDetail(gameDetail: GameDetailDTO, showFavouritesPage: Boolean) {
    Column() {
        AsyncImage(
            model = gameDetail.thumbnail,
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
            items(count = gameDetail.screenshots.size, itemContent = { index ->
                GameScreenshot(screenshotURL = gameDetail.screenshots[index].image)
            })
        }
        //EITHER NOTES OR
        if (showFavouritesPage) Box() {
            Text(text = "FAVOURITES LAYOUT")
        }
        else {
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
                val sysReq = gameDetail.minimum_system_requirements
                val sysReqsMap = hashMapOf(
                    "Graphics:" to sysReq.graphics,
                    "Memory:" to sysReq.memory,
                    "Operating system:" to sysReq.os,
                    "Processor:" to sysReq.processor,
                    "Storage:" to sysReq.storage,
                )
                sysReqsMap.forEach { (key, value) ->
                    Row() {
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

@Composable
fun GameDetailTopBar(mainNavController: NavHostController, gameVM: GameViewModel) {
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
                        gameVM.isLoadingSpecificGame.value = true
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
                text = gameVM.specificGame.title,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFF3E8E8),
                modifier = Modifier.weight(1f),
            )
            if (gameVM.showFavouritesPage) {
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(end = 5.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            mainNavController.popBackStack()
                            gameVM.isLoadingSpecificGame.value = true
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