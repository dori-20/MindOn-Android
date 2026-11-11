package com.example.mindon

import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.w3c.dom.Text
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.w3c.dom.Comment

class CommunityFragment : Fragment() {

    // SharedPreference: 글씨 크기 / 정렬 가져오기
    private fun getTextSizePref(context: Context): Float {
        val prefs = context.getSharedPreferences("community_prefs", Context.MODE_PRIVATE)
        return when (prefs.getString("text_size", "medium")) {
            "small" -> 14f
            "large" -> 20f
            else -> 16f
        }
    }

    private fun getTextAlignPref(context: Context): TextAlign {
        val prefs = context.getSharedPreferences("community_prefs", Context.MODE_PRIVATE)
        return when (prefs.getString("alignment", "start")) {
            "center" -> TextAlign.Center
            else -> TextAlign.Start
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme { // Material3 적용
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "list") {
                        composable("list") {
                            CommunityScreen(
                                navController = navController,
                                textSize = getTextSizePref(context).sp,
                                textAlign = getTextAlignPref(context)
                            )
                        }
                        composable("write") {
                            CommunityWriteScreen(navController)
                        }
                        composable("detail/{postId}") { backStackEntry ->
                            val postId = backStackEntry.arguments?.getString("postId") ?: ""
                            CommunityDetailScreen(postId = postId, navController = navController)
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun CommunityScreen(
        navController: NavController,
        textSize: androidx.compose.ui.unit.TextUnit,
        textAlign: TextAlign,
        viewModel: CommunityViewModel = viewModel()
    ) {
        val posts by viewModel.postList.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("커뮤니티", style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = { navController.navigate("write") }) {
                    Icon(Icons.Default.Edit, contentDescription = "글쓰기")
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(posts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate("detail/${post.id}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                post.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = textSize,
                                textAlign = textAlign
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = post.content,
                                fontSize = textSize,
                                textAlign = textAlign,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun CommunityWriteScreen(
        navController: NavController,
        viewModel: CommunityViewModel = viewModel()
    ) {
        val context = LocalContext.current
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text("글쓰기", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF78866B),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF78866B)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용을 입력하세요") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF78866B),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF78866B)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF78866B)),
                onClick = {
                    viewModel.addPost(title, content)
                    //  알림 호출 코드

                    val helper = MyNoticicationHelper(context)
                    helper.showNotification("커뮤니티", "게시글이 추가되었습니다.")

                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("등록",color = Color.White)
            }
        }
    }

    // 내부 ViewModel 포함
    class CommunityViewModel : ViewModel() {
        private val db = FirebaseFirestore.getInstance()
        private val _postList = MutableStateFlow<List<Post>>(emptyList())
        val postList: StateFlow<List<Post>> = _postList

        init {
            fetchPosts()
        }

        private fun fetchPosts() {
            db.collection("community_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val posts = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Post::class.java)?.copy(id = doc.id)
                        }
                        _postList.value = posts
                    }
                }
        }

        fun addPost(title: String, content: String) {
            val post = Post(title, content, System.currentTimeMillis())
            db.collection("community_posts").add(post)
        }
    }

    @Composable
    fun CommunityDetailScreen(postId: String, navController: NavController) {
        val db = FirebaseFirestore.getInstance()
        var post by remember { mutableStateOf<Post?>(null) }
        var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }

        LaunchedEffect(postId) {
            db.collection("community_posts").document(postId).get()
                .addOnSuccessListener { document ->
                    post = document.toObject(Post::class.java)
                }

            db.collection("community_posts").document(postId)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        comments = snapshot.documents.mapNotNull {
                            it.toObject(Comment::class.java)
                        }
                    }
                }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFB4C9B3),
                    title = { Text("게시글 보기", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            post?.let {
                val context = LocalContext.current
                var commentText by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(it.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it.content)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("댓글", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(comments) { comment ->
                            Text("• ${comment.content}", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("댓글 입력") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF78866B),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF78866B)
                    )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF78866B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            if (commentText.isNotBlank()) {
                                val comment = mapOf(
                                    "content" to commentText,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                db.collection("community_posts")
                                    .document(postId)
                                    .collection("comments")
                                    .add(comment)
                                    .addOnSuccessListener {
                                        commentText = ""
                                        Toast.makeText(context, "댓글이 등록되었습니다!", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                        }
                    ) {
                        Text("등록", color = Color.White)
                    }
                }
            }
        }
    }


    // 데이터 모델
    data class Post(
        val title: String = "",
        val content: String = "",
        val timestamp: Long = 0L,
        val id: String = ""

    )

    data class Comment(
        val content: String = "",
        val timestamp: Long = 0L
    )
}