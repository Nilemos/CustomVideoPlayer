package com.example.customvideoplayer

import android.R.attr.content
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants

@Preview
@Composable
fun VideoList(
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(2) {
            //videoplayer general item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(8.dp),
            ) {
                //video preview picture
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(128.dp)

                ){
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = ImageVector.vectorResource(R.drawable.outline_1k_24),
                        contentDescription = "preview"
                    )
                }
                //video information texts
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                        Text("Text1", color = Color.LightGray)

                        Text("Text2", color = Color.LightGray)
                    Spacer(modifier = Modifier.padding(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text(
                            "Text3", color = Color.LightGray, modifier = Modifier.align(Alignment.CenterStart),
                        )
                        Text("Text4", color = Color.LightGray, modifier = Modifier.align(Alignment.CenterEnd))
                    }


                }
            }
        }
    }
}