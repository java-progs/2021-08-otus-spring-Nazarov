import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import Menu from './menu';

class CommentEdit extends Component {

    booksPath = '/api/books';
    commentsPath = '/comments';

    booksRoute = '/books';
    commentsRoute = '/comments';

    emptyComment = {
        id: '',
        author: '',
        text: ''
    };

    constructor(props) {
        super(props);

        if (this.props.match.params.id && this.props.match.params.id.length > 0) {
            this.emptyComment.id = this.props.match.params.id;
        }

        this.state = ({ comment: this.emptyComment,
                        bookId: this.props.match.params.bookId,
                        isLoading: true,
                        error: { status: false, message: '' } });
        this.handleInput = this.handleInput.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }

    componentDidMount() {
        const bookId = this.state.bookId;
        const comment = this.state.comment;

        this.setState({ isLoading: true });

        if (comment.id && comment.id.length > 0) {
            const commentPath = this.booksPath + '/' + bookId + this.commentsPath + '/' + comment.id;
            fetch(commentPath, { method: 'GET', header: { 'Accept': 'application/json' }
                 })
                 .then(response => {
                    if (!response.ok) {
                        const error = response.json() || response.statusText;
                        return Promise.reject(error);
                    }

                    return response.json();
                 })
                 .then(data => { this.setState({ bookId: bookId });
                                 this.setState({ comment: data });
                                 this.setState({ isLoading: false });
                               },
                       error => { this.setState({ error: { status: true, message: error.toString() } });
                                  this.setState({ isLoading: false });
                                }
                 );
        } else {
            this.setState({ isLoading: false });
        }
    }

    handleInput(event) {
        const comment = this.state.comment;
        const fieldName = event.target.name;
        const fieldValue = event.target.value;

        comment[fieldName] = fieldValue;
        this.setState( { comment: comment });
    }

    handleSubmit(event) {
        event.preventDefault();
        const comment = this.state.comment;
        const commentPath = this.booksPath + '/' + this.state.bookId + this.commentsPath
        const path = comment.id ? commentPath + '/' + comment.id : commentPath;
        const method = comment.id ? 'PUT' : 'POST';

        fetch(path,
              { method: method,
                headers: { 'Accept': 'application/json',
                           'Content-type': 'application/json' },
                body: JSON.stringify(comment)
              })
              .then(response => { this.props.history.push(this.booksRoute + '/' + this.state.bookId + this.commentsRoute); },
                    error => { Promise.reject(response.statusText); })
              .catch(error => {
                    this.setState({ error: { status: true, message: error.toString() } })
              })
    }

    handleCancel(event) {
        event.preventDefault();
        this.props.history.push(this.booksRoute + '/' + this.state.bookId + this.commentsRoute);
    }

    render() {
        const comment = this.state.comment;
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

        return (
            <div>
                <Menu />
                { comment.id ? <div className="header">Edit comment</div> : <div className="header">New comment</div> }
                <form>
                    <table className="tableEdit">
                        <tbody>
                        <tr>
                            <td>Author:</td>
                            <td>{ comment.id && comment.id.length > 0 ? <p className="fieldValue">{comment.author}</p> : <input type="text" name="author" onChange={this.handleInput} /> }</td>
                        </tr>
                        <tr>
                            <td>Text:</td>
                            <td><textarea name="text" value = {comment.text || ''} onChange={this.handleInput} /></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <button className="btnAction" onClick={this.handleSubmit}>Save</button>
                                <button className="btnAction" onClick={this.handleCancel}>Cancel</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        )
    }
}

export default CommentEdit;