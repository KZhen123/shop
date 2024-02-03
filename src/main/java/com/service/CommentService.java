package com.service;

import com.entity.Category;
import com.entity.Comment;
import com.mapper.CommentMapper;
import com.vo.CommentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 评论 服务类
 * </p>
 *
 * @author hlt
 * @since 2019-12-21
 */
@Service
@Transactional
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 插入评论
     */
    public Integer insertComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }

    /**
     * 查询评论
     */
    public List<Comment> queryComments(String commid) {
        return commentMapper.queryComments(commid);
    }

    /**
     * 查询评论中用户信息
     */
    public Comment queryById(String cid) {
        return commentMapper.queryById(cid);
    }

    /**
     * 删除评论
     */
    public Integer deleteComment(String cid) {
        return commentMapper.deleteComment(cid);
    }

    public List<Comment> queryPage(int page, int size, String content) {
        int begin = (page - 1) * size;
        List<Comment> comments = commentMapper.queryPageList(begin, size, content);
        return comments;
    }

    public int getCount(String content) {
        return commentMapper.getCount(content).intValue();
    }

    public boolean setInvalid(String cid) {
        return commentMapper.setInvalid(cid);
    }

    public boolean setValid(String cid) {
        return commentMapper.setValid(cid);
    }
}
