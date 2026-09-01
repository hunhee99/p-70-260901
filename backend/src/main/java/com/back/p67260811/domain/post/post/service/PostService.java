package com.back.p67260811.domain.post.post.service;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.post.comment.entity.PostComment;
import com.back.p67260811.domain.post.post.entity.Post;
import com.back.p67260811.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(Member author, String title, String content) {
        Post post = new Post(author, title, content);
        return postRepository.save(post);
    }

    public PostComment writeComment(Post post, String content) {
        return post.addComment(content);
    }

    public void deleteComment(Post post, int commentId) {
        post.removeComment(commentId);
    }

    public PostComment modifyComment(Post post, int commentId, String content) {
        return post.modifyComment(commentId, content);
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    public List<Post> findAll() {
        return postRepository.findAll().reversed();
    }

    public long count() {
        return postRepository.count();
    }
    public void flush() {
        postRepository.flush();
    }

    public PostComment findCommentById(Post post, int commentId) {
        return post.findCommentById(commentId);
    }

    public void delete(int id) {
        postRepository.deleteById(id);
    }

    public void modify(Post post, String title, String content) {
        post.update(title, content);
    }
}