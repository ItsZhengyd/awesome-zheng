前端请求如图
![image](https://github.com/ItsZhengyd/awesome-zheng/assets/68491620/35698f20-b5a6-4966-ba13-7e31f9d91b06)
参数如下
```
query bookDetails {
  bookById(id: "book-1") {
    id
    name
    pageCount
    author {
      id
      firstName
      lastName
    }
  }
}
mutation createBook {
  createBook(book: {
    name: "New Book Title",
    authorId: "author-4"  # Replace with the actual author ID
  }) {
    id
    name
    pageCount
    author {
      id
      firstName
      lastName
    }
  }
}
mutation updateBook {
  updateBook(id: "book-1", book: {
    name: "Updated Book Title",
    authorId: "author-2"  # Replace with the new author ID
  }) {
    id
    name
    pageCount
    author {
      id
      firstName
      lastName
    }
  }
}
mutation deleteBook {
  deleteBook(id: "book-1")
}
```
