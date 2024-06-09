package com.example.mysns;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button buttonChoose; // 이미지 선택 버튼
    private Button buttonPost; // 게시물 올리기 버튼
    private EditText editText; // 게시물 텍스트 입력란
    private LinearLayout galleryLayout; // 게시물 갤러리 레이아웃
    private List<Post> posts; // 게시물 목록

    private Uri selectedImageUri; // 선택된 이미지 URI를 저장하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChoose = findViewById(R.id.buttonChoose);
        buttonPost = findViewById(R.id.buttonPost);
        editText = findViewById(R.id.editText);
        galleryLayout = findViewById(R.id.galleryLayout);

        posts = new ArrayList<>();

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = editText.getText().toString().trim();
                if (!postText.isEmpty() || selectedImageUri != null) { // 텍스트 또는 이미지가 있어야 게시물을 올릴 수 있음
                    addPostToGallery(postText, selectedImageUri);
                }
            }
        });
    }

    // 갤러리에서 이미지 선택을 위한 메소드
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // 이미지 선택 후 이미지뷰에 선택한 이미지를 표시할 수도 있음
        }
    }

    // 게시물을 갤러리에 추가하는 메소드
    private void addPostToGallery(String postText, Uri imageUri) {
        editText.setText(""); // 텍스트 입력 필드 비우기
        Post post = new Post(postText, imageUri);
        posts.add(post);
        displayPosts();
    }

    // 게시물을 화면에 표시하는 메소드
    private void displayPosts() {
        galleryLayout.removeAllViews();
        for (final Post post : posts) {
            View postView = getLayoutInflater().inflate(R.layout.post_item, null);
            ImageView imageView = postView.findViewById(R.id.imageView);
            TextView textView = postView.findViewById(R.id.textView);
            Button likeButton = postView.findViewById(R.id.likeButton);
            Button commentButton = postView.findViewById(R.id.commentButton);
            Button deletePostButton = postView.findViewById(R.id.deletePostButton); // 삭제 버튼 추가
            LinearLayout commentsLayout = postView.findViewById(R.id.commentsLayout); // 댓글을 추가할 레이아웃 추가

            if (post.getImageUri() != null) {
                imageView.setImageURI(post.getImageUri());
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
            textView.setText(post.getText());

            likeButton.setText("좋아요 (" + post.getLikes() + ")");
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post.like();
                    displayPosts();
                }
            });

            // 길게 눌렀을 때 좋아요 감소
            likeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    post.unlike();
                    displayPosts();
                    return true; // 이벤트 처리를 소비했음을 반환
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCommentDialog(post);
                }
            });

            // 게시물 삭제 버튼에 대한 클릭 리스너 추가
            deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posts.remove(post);
                    displayPosts();
                }
            });

            // 댓글 추가
            commentsLayout.removeAllViews(); // 먼저 레이아웃을 지우고 다시 추가
            for (int i = 0; i < post.getComments().size(); i++) {
                final String comment = post.getComments().get(i);
                TextView commentTextView = new TextView(MainActivity.this);
                commentTextView.setText("댓글 : " + comment);

                // 댓글 삭제 버튼ㅎ
                Button deleteButton = new Button(MainActivity.this);
                deleteButton.setText("댓글 삭제");
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 댓글 삭제 로직 추가
                        post.removeComment(comment);
                        displayPosts();
                    }
                });

                // 댓글과 삭제 버튼을 레이아웃에 추가
                LinearLayout commentLayout = new LinearLayout(MainActivity.this);
                commentLayout.setOrientation(LinearLayout.HORIZONTAL);
                commentLayout.addView(commentTextView);
                commentLayout.addView(deleteButton);

                commentsLayout.addView(commentLayout);
            }

            galleryLayout.addView(postView);
        }
    }

    // 댓글 다이얼로그를 표시하는 메소드
    private void showCommentDialog(final Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("댓글 입력");

        // 입력 필드 설정
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // 버튼 설정
        builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String commentText = input.getText().toString();
                if (!commentText.isEmpty()) {
                    post.addComment(commentText);
                    displayPosts();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}