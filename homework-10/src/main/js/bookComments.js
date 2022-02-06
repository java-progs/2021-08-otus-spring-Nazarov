import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import Menu from './menu';

class BookComments extends Component {

    booksPath = '/api/books';
    commentsPath = '/comments';

    booksRoute = '/books';
    commentsRoute = '/comments'

    constructor(props) {
        super(props);
        this.state = ({ book: null,
                        commentsList: [],
                        isLoading: true,
                        error: { status: false, message: '' } });
        this.openPath = this.openPath.bind(this);
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        this.setState({ isLoading: true });
        const id = this.props.match.params.bookId;

        Promise.all([
            fetch(this.booksPath + `/${id}`, { method: 'GET', headers: { 'Accept': 'application/json' } })
                        .then(response => response.ok ? response.json() : Promise.reject(response.status))
                        .then(data => this.setState({ book: data })),
            fetch(this.booksPath + `/${id}` + this.commentsPath,
                            { method: 'GET', headers: { 'Accept': 'application/json' } })
                        .then(response => response.ok ? response.json() : Promise.reject(response.status))
                        .then(data => this.setState({ commentsList: data }))
        ]).then(result => this.setState({ isLoading: false }),
                error => {
                    this.setState({ error: { status: true, message: error } });
                    this.setState({ isLoading: false });
                });
    }

    loadComments(bookId) {
        fetch(this.booksPath + `/${bookId}` + this.commentsPath,
            { method: 'GET', headers: { 'Accept': 'application/json' } })
            .then(response => response.ok ? response.json() : Promise.reject(response.status))
            .then(data => this.setState({ commentsList: data }))
    }

    openPath(path) {
        this.props.history.push(path);
    }

    remove(commentId) {
        const path = this.booksPath + '/' + this.state.book.id + this.commentsPath + '/' + commentId;
        fetch(path, { method: 'DELETE', headers: { 'Accept': 'application/json' }
              })
              .then(() => this.loadComments(this.state.book.id));
    }

    handleSubmit(event) {
        event.preventDefault();
        const book = this.state.book;
        const path = book.id ? this.booksPath + '/' + book.id : this.booksPath;
        const method = book.id ? 'PUT' : 'POST';

        fetch(path,
              { method: method,
                headers: { 'Accept': 'application/json',
                           'Content-type': 'application/json' },
                body: JSON.stringify(book)
              })
              .then(response => { this.props.history.push(this.booksRoute); },
                    error => { Promise.reject(response.statusText); })
              .catch(error => {
                    this.setState({ error: { status: true, message: error.toString() } })
              })
    }

    render() {
        const book = this.state.book;
        const commentsList = this.state.commentsList;
        const isLoading = this.state.isLoading;
        const error = this.state.error;

        if (isLoading) {
            return (
                <div>
                    <Menu />
                    <h2>Loading...</h2>
                </div>
            )
        }

        if (error.status) {
            return (
                <div>
                    <Menu />
                    <h2>Error: {error.message}</h2>
                </div>
            )
        }

        const bookComments = commentsList.map( comment => {
            return (
                <div>
                    <table key={comment.id}>
                        <tbody>
                        <tr>
                            <td><b>Author:</b></td>
                            <td>{comment.author}</td>
                        </tr>
                        <tr>
                            <td><b>Date:</b></td>
                            <td>{comment.time}</td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>{comment.text}</td>
                        </tr>
                        <tr>
                            <td></td>
                            <td><button className="btnAction" onClick={
                                () => this.openPath(this.booksRoute + '/' + book.id + this.commentsPath + '/' + comment.id) }>Edit</button>
                            <button className="btnAction" onClick={() => this.remove(comment.id)}>Delete</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            )
        });

        return (
            <div>
                <Menu />
                <div className="header">Comments for the book {book.name}</div>
                <button className="btn" onClick={ () => this.openPath(this.booksRoute + '/' + book.id  + this.commentsRoute + '/new') }>New comment</button>
                { commentsList.length > 0 &&
                 <div>
                    {bookComments}
                 </div>
                }
            </div>
        )
    }
}

export default BookComments;