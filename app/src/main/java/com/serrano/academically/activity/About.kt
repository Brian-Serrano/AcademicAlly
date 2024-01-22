package com.serrano.academically.activity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.academically.R
import com.serrano.academically.custom_composables.HorizontalPagerIndicator
import com.serrano.academically.custom_composables.MainButton
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme
import com.serrano.academically.utils.AboutText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun About(navController: NavController) {

    val text = listOf(
        AboutText(
            title = "About AcademicAlly",
            description = "Welcome to AcademicAlly, where learning knows no bounds! We believe in the power of collaborative education and the transformative impact it can have on individuals. Here's a glimpse into what sets us apart:"
        ),
        AboutText(
            title = "Mission: Empowering Minds, Connecting Hearts",
            description = "At AcademicAlly, our mission is to create a global community where learners and tutors come together to inspire, guide, and learn from one another. We're passionate about fostering an environment that transcends traditional boundaries, making education accessible and enjoyable for everyone."
        ),
        AboutText(
            title = "Global Reach, Local Impact",
            description = "With a diverse and inclusive community spanning the globe, AcademicAlly brings together people from different backgrounds, cultures, and experiences. Our platform is a melting pot of knowledge where learners connect with tutors, transcending geographical constraints to create a truly global classroom."
        ),
        AboutText(
            title = "The Heart of Peer-to-Peer Tutoring",
            description = "We recognize the power of peer-to-peer tutoring in driving academic excellence. Our platform is designed to facilitate meaningful connections between learners and tutors, creating an environment where knowledge is shared organically and collaboratively."
        ),
        AboutText(
            title = "Key Features:",
            description = "Personalized Learning: Tailor your learning experience to suit your individual needs, choosing tutors who align with your learning style and goals.\n" +
                    "\n" +
                    "Two-Way Street: Learning is a two-way street at AcademicAlly. Tutors not only share their expertise but also learn and grow through the unique perspectives of their learners.\n" +
                    "\n" +
                    "Flexible and Convenient: We understand the demands of modern life. AcademicAlly offers flexibility, allowing learners and tutors to connect at times that suit their schedules."
        ),
        AboutText(
            title = "Commitment to Safety and Privacy",
            description = "Your security is our priority. AcademicAlly employs robust measures to ensure the confidentiality and privacy of your data, creating a safe and trustworthy space for learning and collaboration."
        ),
        AboutText(
            title = "Community Building and Support",
            description = "Beyond tutoring sessions, AcademicAlly is a community where connections flourish. Engage in forums, join interest groups, and build lasting relationships with fellow learners and tutors. Our support team is always ready to assist you on your educational journey."
        ),
        AboutText(
            title = "Join AcademicAlly Today",
            description = "Whether you're on a quest for knowledge or eager to share your expertise, AcademicAlly is the platform where educational aspirations come to life. Come be a part of our dynamic community and experience the joy of collaborative learning."
        )
    )

    val pagerState = rememberPagerState(pageCount = { text.size })

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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = text[it].title,
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                        Text(
                            text = text[it].description,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        )
                    }
                }
                HorizontalPagerIndicator(pagerState)
                Box(modifier = Modifier.padding(bottom = 20.dp)) {
                    MainButton(
                        text = "SIGN IN",
                        route = "Main",
                        color = MaterialTheme.colorScheme.primary,
                        navController = navController
                    )
                }
            }
        }
    }
}