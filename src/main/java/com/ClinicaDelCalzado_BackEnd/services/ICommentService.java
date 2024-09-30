package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.CommentDTO;
import com.ClinicaDelCalzado_BackEnd.entity.Comment;

import java.util.List;

public interface ICommentService {
    Comment save(Comment comment);
    List<CommentDTO> getCommentOrder(String orderNumber);
    Comment saveCommentOrder(String comment, String orderNumber, Long userAuth);
}
