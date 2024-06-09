package com.example.mysns;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String text; // 게시물 텍스트
    private Uri imageUri; // 게시물 이미지
    private int likes; // 좋아요 수
    private List<String> comments; // 댓글 목록

    // 생성자
    public Post(String text, Uri imageUri) {
        this.text = text;
        this.imageUri = imageUri;
        this.likes = 0;
        this.comments = new ArrayList<>();
    }

    // 게시물 텍스트
    public String getText() {
        return text;
    }

    // 게시물 텍스트
    public void setText(String text) {
        this.text = text;
    }

    // 게시물 이미지
    public Uri getImageUri() {
        return imageUri;
    }

    // 게시물 이미지
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    // 좋아요 수
    public int getLikes() {
        return likes;
    }

    // 좋아요 수
    public void setLikes(int likes) {
        this.likes = likes;
    }

    // 댓글 목록
    public List<String> getComments() {
        return comments;
    }

    // 댓글 추가
    public void addComment(String comment) {
        comments.add(comment);
    }

    // 댓글 삭제
    public void removeComment(String comment) {
        comments.remove(comment);
    }

    // 좋아요 증가
    public void like() {
        likes++;
    }

    // 좋아요 감소
    public void unlike() {
        likes--;
    }
}