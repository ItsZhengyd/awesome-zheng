package com.example.demo;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {
    @QueryMapping
    public Book bookById(@Argument String id) {
        return Book.getById(id);
    }

    @MutationMapping
    public Book createBook(@Argument Book book) {
        // 创建新的Book实例，可能需要一个工厂方法来处理输入数据
        Book newBook = book;
        // 保存新的Book实例到数据存储
        // 这里假设有一个适当的数据存储或服务来处理
        // 你需要根据你的应用程序需求来实现这一部分
        // 返回新创建的Book实例
        return newBook;
    }

    @MutationMapping
    public Book updateBook(@Argument String id, @Argument Book book) {
        // 获取要更新的Book实例
        Book bookToUpdate = Book.getById(id);
        if (bookToUpdate != null) {
            // 更新Book的属性
//            book.setTitle(bookInput.getTitle());
//            book.setAuthorId(bookInput.getAuthorId());
            // 保存更新后的Book实例到数据存储
            // 这里假设有一个适当的数据存储或服务来处理
            // 你需要根据你的应用程序需求来实现这一部分
            // 返回更新后的Book实例
            return bookToUpdate;
        } else {
            // 如果找不到要更新的Book实例，可以选择返回null或抛出异常
            return null;
        }
    }

    @MutationMapping
    public boolean deleteBook(@Argument String id) {
        // 获取要删除的Book实例
        Book bookToDelete = Book.getById(id);
        if (bookToDelete != null) {
            // 删除Book实例从数据存储
            // 这里假设有一个适当的数据存储或服务来处理
            // 返回true表示删除成功
            return true;
        } else {
            // 如果找不到要删除的Book实例，可以选择返回false或抛出异常
            return false;
        }
    }

    @SchemaMapping
    public Author author(Book book) {
        return Author.getById(book.getAuthorId());
    }
}
