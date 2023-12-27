package com.serrano.academically.activity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.serrano.academically.R
import com.serrano.academically.custom_composables.HorizontalPagerIndicator
import com.serrano.academically.custom_composables.MainButton
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.AboutText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun About(navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { 4 })
    val text = arrayOf(
        AboutText(Strings.about1, Strings.loremIpsum),
        AboutText(Strings.about2, Strings.loremIpsum),
        AboutText(Strings.about3, Strings.loremIpsum),
        AboutText(Strings.about4, Strings.loremIpsum)
    )
    SelectionContainer {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .weight(0.5f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondary)
                        .weight(1.5f)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.about),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalPager(state = pagerState) {
                    Column {
                        Text(
                            text = text[it].title,
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                        Text(
                            text = text[it].description,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
                HorizontalPagerIndicator(pagerState)
                Box(modifier = Modifier.padding(bottom = 20.dp)) {
                    MainButton(
                        text = Strings.signIn,
                        route = "Main",
                        color = MaterialTheme.colorScheme.primary,
                        navController = navController
                    )
                }
            }
        }
    }
}