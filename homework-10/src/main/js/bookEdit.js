import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import Menu from './menu';

class BookEdit extends Component {

    booksPath = '/api/books';
    authorsPath = '/api/authors';
    genresPath = '/api/genres';

    booksRoute = '/books';

    emptyBook = {
        id: '',
        name: '',
        isbn: '',
        authorsId: [],
        genresId: []
    };

    constructor(props) {
        super(props);

        if (this.props.match.params.id && this.props.match.params.id.length > 0) {
            this.emptyBook.id = this.props.match.params.id;
        }

        this.state = ({ book: this.emptyBook,
                        authorsList: [],
                        genresList: [],
                        newAuthorId: '',
                        newGenreId: '',
                        isLoading: true,
                        error: { status: false, message: '' } });
        this.handleInput = this.handleInput.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleAdding = this.handleAdding.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }

    componentDidMount() {
        const book = this.state.book;
        this.setState({ isLoading: true });

        if (book.id && book.id.length > 0) {
            Promise.all([
                this.loadBook(book.id),
                this.loadAuthorsAndGenres()
            ])
            .then(this.setState({ isLoading: false }));
        } else {
            Promise.all([
                this.loadAuthorsAndGenres()
            ])
            .then(this.setState({ isLoading: false }));
        }
    }

    loadAuthorsAndGenres() {
        this.setState({ error: { status: false, message: ' ' } });

        Promise.all([
            fetch(this.authorsPath, {method:'GET', headers:{'Accept':'application/json'}})
                .then(response => response.ok ? response.json() : Promise.reject())
                .then(data => this.setState({authorsList: data})),
            fetch(this.genresPath, {method:'GET', headers:{'Accept':'application/json'}})
                .then(response => response.ok ? response.json() : Promise.reject())
                .then(data => this.setState({genresList: data})),
        ]).catch(error => this.setState({error:{status: true, message: error}}));
    }

    loadBook(id) {
        fetch(this.booksPath + `/${id}`,
            { method: 'GET',
              header: { 'Accept': 'application/json' }
            })
            .then(response => {
                if (!response.ok) {
                    const error = response.json() || response.statusText;
                    return Promise.reject(error);
                }

                return response.json();
            })
            .then(data => { this.setState({ book: data }); },
                  error => { this.setState({ error: { status: true, message: error.toString() } }); }
            );
    }

    handleInput(event) {
        const book = this.state.book;
        const fieldName = event.target.name;
        const fieldValue = event.target.value;

        book[fieldName] = fieldValue;
        this.setState( { book: book });
    }

    handleChange(event) {
        const selectName = event.target.name;
        const selectedValue = event.target.value;

        if (selectName === 'newAuthor') {
            this.setState( { newAuthorId: selectedValue });
        } else if (selectName === 'newGenre') {
            this.setState( { newGenreId: selectedValue });
        }
    }

    handleAdding(event) {
        event.preventDefault();
        const buttonName = event.target.name;
        const book = this.state.book

        if (buttonName === 'addAuthor') {
            if (this.state.newAuthorId != '') {
                book.authorsId.push(this.state.newAuthorId);
                this.setState( { book: book } );
            }
        } else if (buttonName === 'addGenre') {
            if (this.state.newGenreId != '') {
                book.genresId.push(this.state.newGenreId);
                this.setState( { book: book } );
            }
        }
    }

    handleDelete(event, id) {
        event.preventDefault();
        const buttonName = event.target.name;
        const book = this.state.book;

        if (buttonName === 'deleteAuthor') {
            const index = book.authorsId.indexOf(id);
            if (index > -1) {
                book.authorsId.splice(index, 1);
                this.setState( { book: book });
            }
        } else if (buttonName === 'deleteGenre') {
            const index = book.genresId.indexOf(id);
            if (index > -1) {
                book.genresId.splice(index, 1);
                this.setState( { book: book });
            }
        }
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

    handleCancel(event) {
        event.preventDefault();
        this.props.history.push(this.booksRoute);
    }

    render() {
        const book = this.state.book;
        const authorsList = this.state.authorsList;
        const genresList = this.state.genresList;
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

        const bookAuthors = authorsList.filter( author => {
            return (book.authorsId.indexOf(author.id) > -1); })
            .map( author => {
                return (
                    <div key={author.id}><span className="fieldValue">{author.fullName}</span>
                        <button className="btnDeleteItem" name='deleteAuthor' onClick={(event) => this.handleDelete(event, author.id)}>Delete</button>
                    </div>
                )
            });

        const allowedAuthors = authorsList.filter(author => {
            return (book.authorsId.indexOf(author.id) < 0); })
            .map(author => {
                return (<option key={author.id} value={author.id}>{author.fullName}</option>)
            });

        const bookGenres = genresList.filter( genre => {
            return (book.genresId.indexOf(genre.id) > -1); })
            .map( genre => {
                return (
                    <div key={genre.id}><span className="fieldValue">{genre.name}</span>
                        <button className="btnDeleteItem" name='deleteGenre' onClick={(event) => this.handleDelete(event, genre.id)}>Delete</button>
                    </div>
                )
            });

        const allowedGenres = genresList.filter(genre => {
            return (book.genresId.indexOf(genre.id) < 0); })
            .map (genre => {
                return (<option key={genre.id} value={genre.id}>{genre.name}</option>)
            });

        return (
            <div>
                <Menu />
                { book.id ? <div className="header">Edit book</div> : <div className="header">New book</div> }
                <form>
                    <table className="tableEdit">
                        <tbody>
                        <tr>
                            <td>Name:</td>
                            <td><input type="text" name="name" value = {book.name || ''} onChange={this.handleInput} /></td>
                        </tr>
                        <tr>
                            <td>ISBN:</td>
                            <td><input type="text" name="isbn" value = {book.isbn || ''} onChange={this.handleInput} /></td>
                        </tr>
                        <tr>
                            <td>Authors:</td>
                            <td>
                                <div>{bookAuthors}</div>
                                { allowedAuthors.length > 0 &&
                                <div>
                                    <select name="newAuthor" onChange={this.handleChange}><option />{allowedAuthors}</select>
                                    <button className="btnAddItem" name="addAuthor" onClick={this.handleAdding}>Add</button>
                                </div>
                                }
                            </td>
                        </tr>
                        <tr>
                            <td>Genres:</td>
                            <td>
                                <div>{bookGenres}</div>
                                { allowedGenres.length > 0 &&
                                <div>
                                    <select name="newGenre" onChange={this.handleChange}><option />{allowedGenres}</select>
                                    <button className="btnAddItem" name="addGenre" onClick={this.handleAdding}>Add</button>
                                </div>
                                }
                            </td>
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

export default BookEdit;