package tornaco.apps.shortx.ui.discover.contribute

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ContributorItem(contributor: Contributor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        com.skydoves.landscapist.glide.GlideImage(
            imageModel = { contributor.avatar_url },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 用户名和贡献次数
        Column {
            Text(
                text = contributor.login,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Contributions: ${contributor.contributions}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ContributorList(contributors: List<Contributor>) {
    LazyColumn(Modifier.heightIn(max = 1000.dp)) {
        items(contributors) { contributor ->
            ContributorItem(contributor = contributor)
        }
    }
}